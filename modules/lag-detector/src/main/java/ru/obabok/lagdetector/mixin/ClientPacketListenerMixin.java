package ru.obabok.lagdetector.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.lagdetector.LagDetectorModuleInit;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleSetTime", at = @At("TAIL"))
    private void onTimeEvent(ClientboundSetTimePacket packet, CallbackInfo ci){
        LagDetectorModuleInit.onPacketReceived();
    }

}
