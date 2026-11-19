package ru.obabok.playertab.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.playertab.PlayerTabModuleInit;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal = 0))
    private void render(GuiGraphicsExtractor instance, Font font, Component str, int x, int y, int color, @Local PlayerInfo info){
        if(PlayerTabModuleInit.config.PlayerTab.tabInfo && minecraft.level != null){
            Player player = minecraft.level.getPlayerByUUID(info.getProfile().id());
            String gameMode;

            GameType infoGameMode = info.getGameMode();
            switch (infoGameMode){
                case CREATIVE -> gameMode = "[Cr] ";
                case SURVIVAL -> gameMode = "[Su] ";
                case SPECTATOR -> gameMode = "[Sp] ";
                case ADVENTURE -> gameMode = "[Ad] ";
                default -> gameMode = "[??] ";
            }

            String distance = "";
            if(player != null && player != minecraft.player && minecraft.getCameraEntity() != null){
                 distance = "d:" + player.blockPosition().distChessboard(minecraft.getCameraEntity().blockPosition()) + " ";
            }

            String out = gameMode + distance + "p:" + info.getLatency();
            instance.text(font, Component.literal(out),x - font.width(out) - 10, y, color);
        }
        instance.text(font, str, x, y, color);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void setScaleStart(GuiGraphicsExtractor graphics, int screenWidth, Scoreboard scoreboard, Objective displayObjective, CallbackInfo ci){
        float scale = PlayerTabModuleInit.config.PlayerTab.tabScale;
        graphics.pose().pushMatrix();

        float centerX = screenWidth / 2.0f;
        graphics.pose().translate(centerX * (1.0f - scale), 10.0f * (1.0f - scale));
        graphics.pose().scale(scale, scale);
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void setScaleEnd(GuiGraphicsExtractor graphics, int screenWidth, Scoreboard scoreboard, Objective displayObjective, CallbackInfo ci){
        graphics.pose().popMatrix();
    }
}
