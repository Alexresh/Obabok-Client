package ru.obabok.gtnhchestitemrender.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.client.util.ContainerPrewiew.ClientcommandsDataQueryHandler;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin implements ClientcommandsDataQueryHandler.IClientPlayNetworkHandler {
    @Unique
    private ClientcommandsDataQueryHandler o_client_ccQueryHandler;

    @Inject(
        method = "handleTagQueryPacket",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/DebugQueryHandler;handleResponse(ILnet/minecraft/nbt/CompoundTag;)Z"
        ),
        cancellable = true
    )
    private void o_client_handleTagQueryResponse(ClientboundTagQueryPacket packet, CallbackInfo ci) {
        if (this.oclient_getCCDataQueryHandler().handleQueryResponse(packet.getTransactionId(), packet.getTag())) {
            ci.cancel();
        }
    }

    @Override
    public ClientcommandsDataQueryHandler oclient_getCCDataQueryHandler() {
        if (this.o_client_ccQueryHandler == null) {
            this.o_client_ccQueryHandler = new ClientcommandsDataQueryHandler((ClientPacketListener) (Object) this);
        }
        return this.o_client_ccQueryHandler;
    }
}
