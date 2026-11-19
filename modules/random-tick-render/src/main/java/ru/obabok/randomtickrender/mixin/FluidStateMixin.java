package ru.obabok.randomtickrender.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.randomtickrender.RandomTickRenderModuleInit;

@Mixin(FluidState.class)
public class FluidStateMixin {

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci){
        if(O_ClientInit.config.LevelRender.enabled && RandomTickRenderModuleInit.config.RandomTickRender.fluidState){
            LevelRender.addHighlight(new TimedBlockRender(pos.getX(), pos.getY(), pos.getZ(), RandomTickRenderModuleInit.config.RandomTickRender.showTime, RandomTickRenderModuleInit.config.RandomTickRender.fluidStateTickColor.color));
        }
    }
}
