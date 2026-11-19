package ru.obabok.pickblock.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.obabok.pickblock.PickBlockModuleInit;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @ModifyExpressionValue(
        method = "handlePickItemFromBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack keepBlockState(ItemStack original, @Local BlockState blockState) {
        if (!PickBlockModuleInit.config.PickBlock.copyBlockState || original.isEmpty()) {
            return original;
        }

        BlockItemStateProperties properties = BlockItemStateProperties.EMPTY;
        for (Property<?> property : blockState.getProperties()) {
            properties = properties.with(property, blockState);
        }
        if (!properties.isEmpty()) {
            original.set(DataComponents.BLOCK_STATE, properties);
        }
        return original;
    }
}
