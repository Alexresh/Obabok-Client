package ru.obabok.elytra.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.obabok.elytra.ElytraModuleInit;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyExpressionValue(
        method = "tryToStartFallFlying",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isInLiquid()Z"
        )
    )
    private boolean modifyIsInLiquid(boolean inLiquid) {
        if (!inLiquid) {
            return false;
        }

        Player player = (Player) (Object) this;
        if (ElytraModuleInit.config != null && ElytraModuleInit.config.Elytra != null) {
            if (ElytraModuleInit.config.Elytra.allowWaterTakeoff && player.isInWater()) {
                return false;
            }
            if (ElytraModuleInit.config.Elytra.allowLavaTakeoff && player.isInLava()) {
                return false;
            }
        }

        return inLiquid;
    }
}
