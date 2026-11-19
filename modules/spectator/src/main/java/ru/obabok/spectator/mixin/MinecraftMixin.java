package ru.obabok.spectator.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.obabok.spectator.SpectatorModuleInit;
import ru.obabok.spectator.SpectatorScreen;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;onHotbarSelected(I)V"))
    private void input(SpectatorGui instance, int slot){
        if(SpectatorModuleInit.config.Spectator.newSpectatorScreen){
            Minecraft.getInstance().gui.setScreen(new SpectatorScreen());
        } else {
            Minecraft.getInstance().gui.hud.getSpectatorGui().onHotbarSelected(slot);
        }
    }
}
