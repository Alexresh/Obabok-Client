package ru.obabok.randomtickrender.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.randomtickrender.RandomTickRenderModuleInit;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private void randomTick(LevelChunk chunk, int tickSpeed, CallbackInfo ci, @Local BlockPos pos){
        if(O_ClientInit.config.LevelRender.enabled && RandomTickRenderModuleInit.config.RandomTickRender.all){
            LevelRender.addHighlight(new TimedBlockRender(pos.getX(), pos.getY(), pos.getZ(), RandomTickRenderModuleInit.config.RandomTickRender.showTime, RandomTickRenderModuleInit.config.RandomTickRender.allTickColor.color));
        }
    }
}
