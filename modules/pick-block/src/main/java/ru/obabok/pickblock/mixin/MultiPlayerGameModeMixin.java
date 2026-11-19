package ru.obabok.pickblock.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.References;
import ru.obabok.pickblock.PickBlockModuleInit;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void handleCreativeModeItemAdd(ItemStack clicked, int slot);

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), cancellable = true)
    private void localStatefulPick(BlockPos pos, boolean includeData, CallbackInfo ci) {
        if (!PickBlockModuleInit.config.PickBlock.copyBlockState || includeData) {
            return;
        }

        LocalPlayer player = this.minecraft.player;
        ClientLevel level = this.minecraft.level;
        if (player == null || level == null || !player.hasInfiniteMaterials()) {
            return;
        }

        if (!level.isLoaded(pos)) {
            return;
        }

        BlockState blockState = level.getBlockState(pos);
        ItemStack itemStack = blockState.getCloneItemStack(level, pos, false);
        if (itemStack.isEmpty()) {
            return;
        }

        BlockItemStateProperties properties = BlockItemStateProperties.EMPTY;
        for (Property<?> property : blockState.getProperties()) {
            properties = properties.with(property, blockState);
        }
        if (!properties.isEmpty()) {
            itemStack.set(DataComponents.BLOCK_STATE, properties);
        }

        Inventory inventory = player.getInventory();
        ItemStack[] before = new ItemStack[36];
        for (int i = 0; i < before.length; i++) {
            before[i] = inventory.getItem(i).copy();
        }

        int matchingSlot = inventory.findSlotMatchingItem(itemStack);
        if (matchingSlot != -1) {
            if (Inventory.isHotbarSlot(matchingSlot)) {
                inventory.setSelectedSlot(matchingSlot);
            } else {
                inventory.pickSlot(matchingSlot);
            }
        } else {
            inventory.addAndPickItem(itemStack);
        }

        for (int i = 0; i < before.length; i++) {
            if (!ItemStack.matches(before[i], inventory.getItem(i))) {
                this.handleCreativeModeItemAdd(inventory.getItem(i), i < 9 ? 36 + i : i);
            }
        }

        ci.cancel();
    }
}
