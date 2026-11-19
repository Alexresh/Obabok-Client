package ru.obabok.gtnhchestitemrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.model.SubmitsListener;
import ru.obabok.o_client.client.util.ContainerPrewiew.ClientcommandsDataQueryHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestItemSubmitsListener implements SubmitsListener {

    private record SlotEntry(int slot, ItemStack stack) {}
    private record ContainerPreview(BlockPos pos, List<SlotEntry> items) {}

    private static @Nullable BlockPos previewMainPos;
    private static @Nullable BlockPos previewNeighborPos;
    private static final Map<BlockPos, ContainerPreview> previewCache = new HashMap<>();
    private static final Map<BlockPos, Long> lastPreviewRequestMs = new HashMap<>();
    private static ItemModelResolver ITEM_MODEL_RESOLVER;

    private static final Minecraft minecraft = Minecraft.getInstance();

    @Override
    public void collectSubmits(LevelRenderContext context, PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera) {
        if(ITEM_MODEL_RESOLVER == null) ITEM_MODEL_RESOLVER = new ItemModelResolver(Minecraft.getInstance().getModelManager());
        if(!GtnhChestItemRenderModuleInit.config.GtnhChestItemRender.enabled) return;
        if(minecraft.player == null || minecraft.getCameraEntity() == null || minecraft.level == null) return;
        HitResult hit = minecraft.player.raycastHitResult(10, minecraft.getCameraEntity());
        BlockPos hitPos = hit instanceof BlockHitResult blockHitResult
                && minecraft.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof Container ? blockHitResult.getBlockPos() : null;

        boolean rayStillHitsWatched = false;
        if(previewMainPos != null){
            Vec3 rayEnd = cameraPos.add(minecraft.getCameraEntity().getLookAngle().scale(10.0));
            rayStillHitsWatched = new AABB(previewMainPos).clip(cameraPos, rayEnd).isPresent();
        }

        boolean sameGroup = rayStillHitsWatched || (hitPos != null && (hitPos.equals(previewMainPos) || (previewNeighborPos != null && hitPos.equals(previewNeighborPos))));
        if(!sameGroup){
            previewCache.clear();
            previewMainPos = hitPos;
            previewNeighborPos = null;
        }

        BlockPos targetPos = previewMainPos;
        if(targetPos != null){
            BlockState state = minecraft.level.getBlockState(targetPos);
            if(state.getBlock() instanceof ChestBlock && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE){
                previewNeighborPos = targetPos.relative(ChestBlock.getConnectedDirection(state));
            } else {
                previewNeighborPos = null;
            }

            requestPreview(targetPos);
            BlockPos neighborPos = previewNeighborPos;
            if(neighborPos != null && minecraft.level.getBlockEntity(neighborPos) instanceof Container){
                requestPreview(neighborPos);
            }

            ContainerPreview main = previewCache.get(targetPos);
            ContainerPreview neighbor = previewNeighborPos == null ? null : previewCache.get(previewNeighborPos);
            boolean neighborIsLeft = false;
            if(state.getBlock() instanceof ChestBlock){
                neighborIsLeft = state.getValue(ChestBlock.FACING).getClockWise() == ChestBlock.getConnectedDirection(state);
            }
            if(main != null || neighbor != null){
                submitContainerItems(
                        targetPos,
                        neighborIsLeft,
                        main == null ? List.of() : main.items(),
                        neighbor == null ? List.of() : neighbor.items(),
                        poses,
                        collector,
                        cameraPos
                );
            }
        }
    }

    private static void submitContainerItems(
            BlockPos pos,
            boolean neighborIsLeft,
            List<SlotEntry> mainItems,
            List<SlotEntry> neighborItems,
            PoseStack poses,
            SubmitNodeCollector collector,
            Vec3 cameraPos
    ){
        GtnhChestItemRenderConfig.GtnhChestItemRenderModule config = GtnhChestItemRenderModuleInit.config.GtnhChestItemRender;
        record ChestPart(List<SlotEntry> items, int rowBase) {}

        List<ChestPart> parts = new ArrayList<>();
        if(neighborIsLeft){
            if(!neighborItems.isEmpty()){
                parts.add(new ChestPart(neighborItems, 0));
            }
            if(!mainItems.isEmpty()){
                parts.add(new ChestPart(mainItems, 3));
            }
        } else {
            if(!mainItems.isEmpty()){
                parts.add(new ChestPart(mainItems, 0));
            }
            if(!neighborItems.isEmpty()){
                parts.add(new ChestPart(neighborItems, 3));
            }
        }

        float colStep = config.spacing;
        float rowStep = config.spacing;
        float scale = config.scale;
        int rows = 0;
        for(ChestPart part : parts){
            for(SlotEntry entry : part.items()){
                rows = Math.max(rows, part.rowBase() + entry.slot() / 9 + 1);
            }
        }

        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3 toCamera = cameraPos.subtract(center);
        if(toCamera.lengthSqr() < 1e-8){
            toCamera = new Vec3(0.0, 1.0, 0.0);
        }
        toCamera = toCamera.normalize();
        double yawRad = Math.atan2(toCamera.x, toCamera.z);
        double pitchRad = -Math.asin(Math.clamp(toCamera.y, -1.0, 1.0));
        float yRot = (float) Math.toDegrees(yawRad);
        float xRot = (float) Math.toDegrees(pitchRad);
        Vec3 right = new Vec3(Math.cos(yawRad), 0.0, -Math.sin(yawRad));
        Vec3 up = new Vec3(Math.sin(yawRad) * Math.sin(pitchRad), Math.cos(pitchRad), Math.cos(yawRad) * Math.sin(pitchRad));
        Vec3 front = center.add(toCamera.scale(config.distance));
        int light = LightCoordsUtil.getLightCoords(minecraft.level, pos);

        int index = 0;
        for(ChestPart part : parts){
            for(SlotEntry entry : part.items()){
                int row = part.rowBase() + entry.slot() / 9;
                int column = entry.slot() % 9;
                float offsetX = (column - 4) * colStep;
                float offsetY = ((rows - 1) / 2.0F - row) * rowStep;
                Vec3 itemPos = front.add(right.scale(offsetX)).add(up.scale(offsetY));

                ItemStackRenderState renderState = new ItemStackRenderState();
                ITEM_MODEL_RESOLVER.updateForTopItem(renderState, entry.stack(), ItemDisplayContext.FIXED, minecraft.level, null, index);
                index++;

                poses.pushPose();
                poses.translate(itemPos.x - cameraPos.x, itemPos.y - cameraPos.y, itemPos.z - cameraPos.z);
                poses.rotateDegrees(Axis.YP, yRot);
                poses.rotateDegrees(Axis.XP, xRot);
                poses.rotateDegrees(Axis.YP, 180.0F);
                poses.scale(scale, scale, scale);
                AABB boundingBox = renderState.getModelBoundingBox();
                poses.translate(
                        -(float) (boundingBox.minX + boundingBox.maxX) / 2,
                        -(float) (boundingBox.minY + boundingBox.maxY) / 2,
                        -(float) (boundingBox.minZ + boundingBox.maxZ) / 2
                );
                renderState.submit(poses, collector, light, OverlayTexture.NO_OVERLAY, 0);
                poses.popPose();
            }
        }
    }

    private static void requestPreview(BlockPos pos){
        lastPreviewRequestMs.compute(pos, (key, last) -> {
            long now = System.currentTimeMillis();
            if(last != null && now - last < 500){
                return last;
            }
            ClientPacketListener packetListener = minecraft.getConnection();
            if(packetListener != null){
                ClientcommandsDataQueryHandler.get(packetListener).queryBlockNbt(pos, blockNbt -> {
                    previewCache.put(pos, new ContainerPreview(pos, parseContainerItems(blockNbt)));
                });
            }
            return now;
        });
    }

    private static List<SlotEntry> parseContainerItems(@Nullable CompoundTag blockNbt){
        List<SlotEntry> items = new ArrayList<>();
        if(blockNbt == null || minecraft.level == null || !blockNbt.contains(ContainerHelper.TAG_ITEMS)){
            return items;
        }

        ListTag listTag = blockNbt.getListOrEmpty(ContainerHelper.TAG_ITEMS);
        for(int i = 0; i < listTag.size(); i++){
            CompoundTag compound = listTag.getCompoundOrEmpty(i);
            DataResult<ItemStackTemplate> itemResult = ItemStackTemplate.CODEC.parse(
                    minecraft.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound
            );
            if(itemResult.isSuccess()){
                try {
                    int slot = compound.getIntOr("Slot", i);
                    ItemStack itemStack = itemResult.getOrThrow().create();
                    if(!itemStack.isEmpty()){
                        items.add(new SlotEntry(slot, itemStack));
                    }
                } catch(IllegalStateException exception){
                    References.LOGGER.error(exception.getMessage());
                }
            }
        }
        return items;
    }
}
