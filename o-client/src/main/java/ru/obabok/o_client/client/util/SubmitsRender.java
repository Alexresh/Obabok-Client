package ru.obabok.o_client.client.util;

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
import net.minecraft.network.chat.Component;
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
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.SubmitsListener;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.ContainerPrewiew.ClientcommandsDataQueryHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitsRender {
    private static final List<SubmitsListener> listeners = new ArrayList<>();


    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void collectSubmits(LevelRenderContext context) {
        if (!O_ClientInit.config.LevelRender.enabled || minecraft.level == null) {
            return;
        }

        CameraRenderState camera = context.levelState().cameraRenderState;
        Vec3 cameraPos = camera.pos;
        SubmitNodeCollector collector = context.submitNodeCollector();
        PoseStack poses = context.poseStack();

        poses.pushPose();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).collectSubmits(context, poses, collector, cameraPos, camera);
        }

        submitHighlightsLabels(poses, collector, cameraPos, camera);


        poses.popPose();
    }

    public static void addListener(SubmitsListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(SubmitsListener listener) {
        listeners.remove(listener);
    }





    private static void submitHighlightsLabels(PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera) {
        List<TimedBlockRender> activeHighlights = LevelRender.getActiveHighlights();
        for (int i = 0; i < activeHighlights.size(); i++) {
            if(activeHighlights.get(i).label != null){
                collector.submitNameTag(
                        poses,
                        new Vec3(activeHighlights.get(i).x + activeHighlights.get(i).size / 2 - cameraPos.x, activeHighlights.get(i).y + activeHighlights.get(i).size / 2 + 0.1 - cameraPos.y, activeHighlights.get(i).z + activeHighlights.get(i).size / 2 - cameraPos.z),
                        0,
                        Component.literal(activeHighlights.get(i).label),
                        true,
                        0xF000F0,
                        camera
                );
            }
        }
    }




}
