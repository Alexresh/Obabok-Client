package ru.obabok.o_client.client.util;

import net.minecraft.client.gui.screens.Screen;
import ru.obabok.oui.config.OConfigManager;
import ru.obabok.oui.model.ModuleScreenFactory;
import ru.obabok.oui.model.OBaseScreen;
import ru.obabok.oui.util.TranslationHelper;

public class SoundMuteScreenFactory implements ModuleScreenFactory {
    @Override
    public OBaseScreen create(Screen parent, Object moduleObject, String moduleName, OConfigManager<?> config) {
        //todo create muteScreen
        return new OBaseScreen(TranslationHelper.translateOrGet(moduleName));
    }
}
