package ru.obabok.o_client.client.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.oui.screen.OUniversalSettingsScreen;
import ru.obabok.oui.widget.button.OButtonWidget;


@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen{
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(boolean showPauseMenu, CallbackInfo ci){
        addRenderableWidget(OButtonWidget.builder("OUI")
                .onMouseClick(_ -> minecraft.setScreenAndShow(new OUniversalSettingsScreen("OUI")))
                .pos(10, 10)
                .size(20, 20)
                .build());
    }
}
