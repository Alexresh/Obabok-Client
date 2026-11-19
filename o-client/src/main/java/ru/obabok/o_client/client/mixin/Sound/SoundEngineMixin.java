package ru.obabok.o_client.client.mixin.Sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.obabok.o_client.client.tweaks.SoundMuteManager;

@Environment(EnvType.CLIENT)
@Mixin(SoundManager.class)
public class SoundEngineMixin {

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void muteSound(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir){
        if(instance == null) return;

        SoundMuteManager.onSound(instance.getIdentifier());

        if(SoundMuteManager.isMuted(instance.getIdentifier()))
            cir.cancel();
    }

}
