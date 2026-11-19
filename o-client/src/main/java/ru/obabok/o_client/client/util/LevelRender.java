package ru.obabok.o_client.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.LevelRenderListener;
import ru.obabok.o_client.client.model.TickListener;
import ru.obabok.o_client.client.model.TimedBlockRender;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LevelRender implements TickListener {

    private static final Queue<TimedBlockRender> pendingAdds = new ConcurrentLinkedQueue<>();
    private static final List<TimedBlockRender> activeHighlights = new ArrayList<>();

    private static final Minecraft client = Minecraft.getInstance();

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Waypoints Buffer", RenderType.SMALL_BUFFER_SIZE);
    private static final ArrayList<AABB> collisionBoxes = new ArrayList<>();

    private static final RenderPipeline BLOCKS_RENDER = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(References.MOD_ID, "pipeline/box")).withDepthStencilState(Optional.empty())
            .build()
    );

    private static final List<LevelRenderListener> listeners = new ArrayList<>();

    @Override
    public void onTick(Minecraft minecraft) {
        if(!O_ClientInit.config.LevelRender.enabled) return;

        TimedBlockRender item;
        while ((item = pendingAdds.poll()) != null) {
            activeHighlights.add(item);
        }

        activeHighlights.forEach(timedBlockRender -> {
            if(client.level != null && !client.level.tickRateManager().isFrozen()){
                timedBlockRender.ticks--;
            }
        });

        collisionBoxes.clear();
        if(client.level != null){
            for (int i = 0; i < listeners.size(); i++) {
                try {
                    listeners.get(i).prepare(minecraft);
                } catch (Exception e){
                    References.LOGGER.error("[o-base][LevelRender] Error in module {}\n{}", listeners.get(i).getClass().getName(), e.getMessage());
                }
            }
        }
    }

    public static void addListener(LevelRenderListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(LevelRenderListener listener) {
        listeners.remove(listener);
    }

    public static void render(LevelRenderContext context) {
        VertexFormat formatBinding = BLOCKS_RENDER.getVertexFormatBinding(0);
        if(formatBinding == null) return;
        PrimitiveTopology primitive = BLOCKS_RENDER.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(formatBinding, primitive, primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        render(context, draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if (info != null) {
            draw(info);
        }

        stagedBuffer.endFrame();
//todo add lines
//        if(client.level != null){
//            if(References.getConfig().LevelRender.EntityRender.boundingBox){
//                client.level.entitiesForRendering().forEach(entity -> {
//                    if(client.player != null && !entity.is(client.player)){
//                        Gizmos.line(client.player.getPosition(context.levelState().worldPartialTicks), entity.getPosition(context.levelState().worldPartialTicks), 0xFFFFFFFF);
//                    }
//                });
//            }
//        }
    }

    private static void render(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();

        final VertexConsumer builder = stagedBuffer.getVertexBuilder(draw);

        if(!collisionBoxes.isEmpty()){
            for (int i = 0; i < collisionBoxes.size(); i++) {
                AABB cb = collisionBoxes.get(i);
                renderFilledBox(matrices.last().pose(), builder, camera, cb.minX, cb.minY, cb.minZ, cb.maxX, cb.maxY, cb.maxZ, O_ClientInit.config.LevelRender.boundingBoxColor.color);
            }
        }

        Iterator<TimedBlockRender> iter = activeHighlights.iterator();
        while (iter.hasNext()) {
            TimedBlockRender timedBlock = iter.next();

            if (timedBlock.maxTicks <= 0 || timedBlock.ticks <= 0) {
                iter.remove();
            }

            float progress = (float) timedBlock.ticks / timedBlock.maxTicks;

            int baseAlpha = (timedBlock.color >> 24) & 0xFF;

            if (baseAlpha == 0) {
                baseAlpha = 255;
            }

            int alpha = (int) (progress * baseAlpha);
            alpha = Math.clamp(alpha, 0, 255);

            int color = (alpha << 24) | (timedBlock.color & 0x00FFFFFF);
            renderFilledBox(matrices.last().pose(), builder, camera, timedBlock.x, timedBlock.y, timedBlock.z, timedBlock.x + timedBlock.size, timedBlock.y + timedBlock.size, timedBlock.z + timedBlock.size, color);
        }

        matrices.popPose();
    }

    private static void draw(StagedVertexBuffer.ExecuteInfo info) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = LevelRender.client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> References.MOD_ID + " example render pipeline rendering", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(LevelRender.BLOCKS_RENDER));

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }

    public static void addHighlight(TimedBlockRender timedBlockRender) {
        if(!pendingAdds.contains(timedBlockRender))
            pendingAdds.add(timedBlockRender);
    }

    public static void addCollisionBox(AABB box){
        collisionBoxes.add(box);
    }

    public static int getHighlightCount(){
        return activeHighlights.size();
    }

    public static List<TimedBlockRender> getActiveHighlights(){
        return activeHighlights;
    }

    public static void renderFilledBox(Matrix4fc positionMatrix, VertexConsumer buffer, Vec3 camera, double x1, double y1, double z1, double x2, double y2, double z2, int color) {

        //up
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);

        //down
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);

        //north
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);

        //south
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);

        //west
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x1 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);

        //east
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z2 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y1 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z1 - camera.z)).setColor(color);
        buffer.addVertex(positionMatrix, (float)(x2 - camera.x), (float)(y2 - camera.y), (float)(z2 - camera.z)).setColor(color);
    }

}
