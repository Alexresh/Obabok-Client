package ru.obabok.o_client.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.oui.screen.OUniversalSettingsScreen;
import ru.obabok.oui.widget.button.OButtonWidget;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci){
        addRenderableWidget(OButtonWidget.builder("OUI")
                .pos(10, 10)
                .size(20, 20)
                .onMouseClick(_ -> minecraft.setScreenAndShow(new OUniversalSettingsScreen("OUI"))).build());
    }
}
