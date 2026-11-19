package ru.obabok.particlerender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import ru.obabok.o_client.client.model.LevelRenderListener;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.particlerender.mixin.ParticleEngineAccessor;
import ru.obabok.particlerender.mixin.ParticleGroupAccessor;

import java.util.Map;

public class ParticleLevelRenderListener implements LevelRenderListener {
    @Override
    public void prepare(Minecraft minecraft) {
        if (ParticleRenderModuleInit.config.ParticleRender.boundingBox){
            Map<ParticleRenderType, ParticleGroup<?>> particles = ((ParticleEngineAccessor)minecraft.particleEngine).o_client$getParticles();
            particles.forEach((particleRenderType, particleGroup) -> {
                ((ParticleGroupAccessor<?>)particleGroup).getParticles().forEach(particle -> {
                    LevelRender.addCollisionBox(particle.getBoundingBox());
                });
            });
        }
    }

    @Override
    public void render() {

    }
}
