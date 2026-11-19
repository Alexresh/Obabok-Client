package ru.obabok.favoriteitems.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.favoriteitems.FavoriteItemsStorage;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiplayerGameModeMixin {
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void disableDrop(LocalPlayer player, boolean all, CallbackInfo ci){
       if(FavoriteItemsStorage.isFavoriteItem(player.getMainHandItem())) ci.cancel();
    }
}
