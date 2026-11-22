package ru.obabok.armorhud.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.armorhud.ArmorHudModuleInit;

@Mixin(Hud.class)
public abstract class ArmorHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract Font getFont();

    @Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
    private void renderArmor(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 0) int screenCenter, @Local(ordinal = 2) int halfHotbar){
        if(minecraft.player == null || minecraft.level == null || !ArmorHudModuleInit.config.ArmorHud.armorDamage) return;
        int startX = (ArmorHudModuleInit.config.ArmorHud.right ? screenCenter + halfHotbar : screenCenter - halfHotbar) + ArmorHudModuleInit.config.ArmorHud.offsetX;
        int startY = graphics.guiHeight() + ArmorHudModuleInit.config.ArmorHud.offsetY;

        for (int i = 0; i <= 3; i++) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(startX + (ArmorHudModuleInit.config.ArmorHud.right ? i : -i) * ArmorHudModuleInit.config.ArmorHud.spacing, startY);
            ItemStack itemStack = minecraft.player.getInventory().getItem(i + 36);
            graphics.item(itemStack, 0, 0);
            if(itemStack.isDamaged()){
                graphics.pose().pushMatrix();
                graphics.pose().scale(0.5f);
                String durabilityText = String.valueOf(itemStack.getMaxDamage() - itemStack.getDamageValue());
                int textWidth = getFont().width(durabilityText);
                graphics.text(getFont(), durabilityText, 16 - textWidth / 2, 30, 0xFF000000 | itemStack.getBarColor());
                graphics.pose().popMatrix();
            }

            graphics.pose().popMatrix();
        }

    }
}
