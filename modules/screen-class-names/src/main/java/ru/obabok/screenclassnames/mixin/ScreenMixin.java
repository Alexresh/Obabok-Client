package ru.obabok.screenclassnames.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.CommonColors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.References;
import ru.obabok.screenclassnames.ScreenClassNamesModuleInit;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci){
        if(ScreenClassNamesModuleInit.config.ScreenClassNames.enabled){
            graphics.text(minecraft.font, getClass().getName(), 0, 0, CommonColors.RED);
        }
    }
}
