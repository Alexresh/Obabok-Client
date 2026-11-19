package ru.obabok.itemdurability.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.itemdurability.ItemDurabilityModuleInit;
import ru.obabok.o_client.References;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void text(Font font, Component str, int x, int y, int color);

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Inject(method = "itemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;IIIII)V"))
    private void addDigitalDurability(ItemStack itemStack, int x, int y, CallbackInfo ci){
        if (!ItemDurabilityModuleInit.config.ItemDurability.enabled) return;
        pose.pushMatrix();
        pose.scale(0.5F,0.5F);
        String damage = "" + (itemStack.getMaxDamage() - itemStack.getDamageValue());
        text(minecraft.font, Component.literal(damage), x * 2 + 18 - minecraft.font.width(damage)/2, y * 2 + 17, itemStack.getBarColor() | -16777216);
        pose.popMatrix();
    }

}
