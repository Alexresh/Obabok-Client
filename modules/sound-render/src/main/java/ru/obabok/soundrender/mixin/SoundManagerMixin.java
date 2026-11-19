package ru.obabok.soundrender.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.soundrender.SoundRenderModuleInit;

@Mixin(SoundEngine.class)
public class SoundManagerMixin {

    @Inject(method = "play", at = @At("TAIL"))
    private void play(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir){
        if(O_ClientInit.config.LevelRender.enabled && SoundRenderModuleInit.config.SoundRender.enabled){
            TimedBlockRender soundBlock = new TimedBlockRender(
                    instance.getX() - SoundRenderModuleInit.config.SoundRender.soundEventSize / 2,
                    instance.getY() - SoundRenderModuleInit.config.SoundRender.soundEventSize / 2,
                    instance.getZ() - SoundRenderModuleInit.config.SoundRender.soundEventSize / 2,
                    SoundRenderModuleInit.config.SoundRender.soundRenderTicks,
                    SoundRenderModuleInit.config.SoundRender.soundEventColor.color);
            soundBlock.label = instance.getIdentifier().toString();
            soundBlock.size = SoundRenderModuleInit.config.SoundRender.soundEventSize;
            LevelRender.addHighlight(soundBlock);
        }
    }
}
