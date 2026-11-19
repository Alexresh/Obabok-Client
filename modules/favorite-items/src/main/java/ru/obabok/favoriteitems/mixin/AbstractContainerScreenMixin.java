package ru.obabok.favoriteitems.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLKeycode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.obabok.favoriteitems.FavoriteItemsModuleInit;
import ru.obabok.favoriteitems.FavoriteItemsStorage;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    @Final
    protected T menu;

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void renderFavoriteBackground(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if(!FavoriteItemsModuleInit.config.FavoriteItems.enabled) return;
        ItemStack stack = slot.getItem();
        if(stack.isEmpty() || !FavoriteItemsStorage.isFavoriteItem(stack)) return;
        graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x50FFFFFF & CommonColors.GREEN);
    }

    @Inject(method = "extractCarriedItem", at = @At("TAIL"))
    private void renderCarriedFavorite(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if(!FavoriteItemsModuleInit.config.FavoriteItems.enabled) return;
        ItemStack stack = menu.getCarried();
        if(stack.isEmpty() || !FavoriteItemsStorage.isFavoriteItem(stack)) return;
        graphics.fill(mouseX - 8, mouseY - 8, mouseX + 8, mouseY + 8, 0x50FFFFFF & CommonColors.GREEN);
    }

    @Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if(!FavoriteItemsModuleInit.config.FavoriteItems.enabled) return;
        ItemStack stack = ItemStack.EMPTY;
        if(containerInput == ContainerInput.THROW && slot != null) {
            stack = slot.getItem();
        }

        if(containerInput == ContainerInput.QUICK_MOVE && slot.container instanceof Inventory){
            stack = slot.getItem();
        }

        if(!stack.isEmpty() && FavoriteItemsStorage.isFavoriteItem(stack)){
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("TAIL"))
    private void favoriteItem(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if(!FavoriteItemsModuleInit.config.FavoriteItems.enabled) return;
        if(event.keycode() == SDLKeycode.SDLK_H && hoveredSlot != null && hoveredSlot.hasItem()){
            ItemStack hoveredStack = hoveredSlot.getItem();
            if(FavoriteItemsStorage.isFavoriteItem(hoveredStack)){
                FavoriteItemsStorage.removeFavoriteItem(hoveredStack);
            } else {
                FavoriteItemsStorage.addFavoriteItem(hoveredStack);
            }
        }
    }
}
