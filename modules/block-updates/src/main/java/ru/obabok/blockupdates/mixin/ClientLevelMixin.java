package ru.obabok.blockupdates.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.blockupdates.BlockUpdatesModuleInit;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;


@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "sendBlockUpdated", at = @At("RETURN"))
    private void blockChanged(BlockPos pos, BlockState old, BlockState current, int updateFlags, CallbackInfo ci){
        if(O_ClientInit.config.LevelRender.enabled && BlockUpdatesModuleInit.config.BlockUpdates.packetUpdate) {
            LevelRender.addHighlight(new TimedBlockRender(pos.getX(), pos.getY(), pos.getZ(), BlockUpdatesModuleInit.config.BlockUpdates.showTime, BlockUpdatesModuleInit.config.BlockUpdates.packetUpdateColor.color));
        }
    }
}
