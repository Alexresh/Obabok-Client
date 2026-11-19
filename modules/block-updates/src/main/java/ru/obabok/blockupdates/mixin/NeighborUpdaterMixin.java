package ru.obabok.blockupdates.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.blockupdates.BlockUpdatesModuleInit;import ru.obabok.o_client.References;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.TimedBlockRender;
import ru.obabok.o_client.client.util.LevelRender;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {

    @Inject(method = "executeUpdate", at = @At("HEAD"))
    private static void renderBlockUpdate(Level level, BlockState state, BlockPos pos, Block changedBlock, Orientation orientation, boolean movedByPiston, CallbackInfo ci){
        if(O_ClientInit.config.LevelRender.enabled && BlockUpdatesModuleInit.config.BlockUpdates.neighborUpdate)
            LevelRender.addHighlight(new TimedBlockRender(pos.getX(), pos.getY(), pos.getZ(), 20, BlockUpdatesModuleInit.config.BlockUpdates.neighborUpdateColor.color));
    }

    @Inject(method = "executeShapeUpdate", at = @At("HEAD"))
    private static void renderShapeUpdate(LevelAccessor level, Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, int updateFlags, int updateLimit, CallbackInfo ci){
        if(O_ClientInit.config.LevelRender.enabled && BlockUpdatesModuleInit.config.BlockUpdates.shapeUpdate)
            LevelRender.addHighlight(new TimedBlockRender(pos.getX(), pos.getY(), pos.getZ(), 20, BlockUpdatesModuleInit.config.BlockUpdates.shapeUpdateColor.color));
    }
}
