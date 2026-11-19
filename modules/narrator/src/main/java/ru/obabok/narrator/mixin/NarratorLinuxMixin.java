package ru.obabok.narrator.mixin;


import com.mojang.text2speech.Narrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.obabok.narrator.NarratorModuleInit;

@Mixin(Narrator.class)
public interface NarratorLinuxMixin {
    @Inject(method = "getNarrator", at = @At("HEAD"), cancellable = true)
    private static void suppressThrowable(CallbackInfoReturnable<Narrator> cir){
        if(NarratorModuleInit.config.Narrator.suppressNarratorWarning)
            cir.setReturnValue(Narrator.EMPTY);
    }
}
