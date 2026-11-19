package ru.obabok.o_client.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public interface SubmitsListener {
    void collectSubmits(LevelRenderContext context, PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera);
}
