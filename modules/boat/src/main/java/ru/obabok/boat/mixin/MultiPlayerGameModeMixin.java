package ru.obabok.boat.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.obabok.boat.BoatModuleInit;
import ru.obabok.o_client.References;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Unique
    private float oldRotation = 0f;
    @Unique
    private boolean rotationChanged = false;

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void beforeUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).getItem() instanceof BoatItem && BoatModuleInit.config.Boat.fixedBoatRotation) {
            oldRotation = player.getYRot();
            float snapped = Math.round(oldRotation / 90.0f) * 90.0f;

            player.setYRot(snapped);
            rotationChanged = true;
        }
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void afterUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (rotationChanged) {
            player.setYRot(oldRotation);
            rotationChanged = false;
        }
    }

    @Inject(method = "useItem", at = @At("HEAD"))
    private void beforeUseItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).getItem() instanceof BoatItem && BoatModuleInit.config.Boat.fixedBoatRotation) {
            oldRotation = player.getYRot();
            float snapped = Math.round(oldRotation / 90.0f) * 90.0f;
            player.setYRot(snapped);
            rotationChanged = true;
        }
    }

    @Inject(method = "useItem", at = @At("RETURN"))
    private void afterUseItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (rotationChanged) {
            player.setYRot(oldRotation);
            rotationChanged = false;
        }
    }
}
