package ru.obabok.explosionrender.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.explosionrender.ExplosionRenderModuleInit;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleExplosion", at = @At("RETURN"))
    private void highlightExplosion(ClientboundExplodePacket packet, CallbackInfo ci){
        if(O_ClientInit.config.LevelRender.enabled && ExplosionRenderModuleInit.config.ExplosionRender.enabled){
            TimedBlockRender timedBlockRender = new TimedBlockRender(packet.center().x, packet.center().y, packet.center().z, ExplosionRenderModuleInit.config.ExplosionRender.ticks, ExplosionRenderModuleInit.config.ExplosionRender.color.color);
            timedBlockRender.size =  ExplosionRenderModuleInit.config.ExplosionRender.size;
            timedBlockRender.label = "x: " + packet.center().x + " y:" + packet.center().y + " z:" + packet.center().z + " bc:" + packet.blockCount() + " r:" + packet.radius();
            LevelRender.addHighlight(timedBlockRender);
        }
    }
}
