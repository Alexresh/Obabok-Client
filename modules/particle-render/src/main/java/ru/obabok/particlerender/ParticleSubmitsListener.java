package ru.obabok.particlerender;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ru.obabok.o_client.client.model.SubmitsListener;
import ru.obabok.particlerender.mixin.ParticleEngineAccessor;
import ru.obabok.particlerender.mixin.ParticleGroupAccessor;

public class ParticleSubmitsListener implements SubmitsListener {
    @Override
    public void collectSubmits(LevelRenderContext context, PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera) {
        if(ParticleRenderModuleInit.config.ParticleRender.particleName){
            var particles = ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).o_client$getParticles();
            particles.forEach((particleRenderType, particleGroup) -> {
                ((ParticleGroupAccessor<?>) particleGroup).getParticles().forEach(particle -> {
                    AABB box = particle.getBoundingBox();
                    double x = (box.minX + box.maxX) / 2.0 - cameraPos.x;
                    double y = box.maxY + 0.1 - cameraPos.y;
                    double z = (box.minZ + box.maxZ) / 2.0 - cameraPos.z;

                    collector.submitNameTag(
                            poses,
                            new Vec3(x, y, z),
                            0,
                            Component.literal(getParticleName(particle)),
                            true,
                            0xF000F0,
                            camera
                    );
                });
            });
        }
    }

    private static String getParticleName(Particle particle) {
        String name = particle.getClass().getSimpleName();
        return name.endsWith("ParticleModule") ? name.substring(0, name.length() - 8) : name;
    }
}
