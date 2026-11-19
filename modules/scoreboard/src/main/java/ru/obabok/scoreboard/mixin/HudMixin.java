package ru.obabok.scoreboard.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.o_client.References;
import ru.obabok.scoreboard.ScoreboardModuleInit;

@Environment(EnvType.CLIENT)
@Mixin(Hud.class)
public class HudMixin {

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"))
    private void sidebarRenderStart(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        float scale = ScoreboardModuleInit.config.Scoreboard.scoreboard_scale;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        graphics.pose().pushMatrix();

        float tx = width * (1.0f - scale);
        float ty = (height / 2.0f) * (1.0f - scale);

        graphics.pose().translate(tx, ty);
        graphics.pose().scale(scale, scale);
    }


    @Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
    private void sidebarRenderEnd(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        graphics.pose().popMatrix();
    }
}
