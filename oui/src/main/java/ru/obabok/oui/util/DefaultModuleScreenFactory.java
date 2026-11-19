package ru.obabok.oui.util;

import net.minecraft.client.gui.screens.Screen;
import ru.obabok.oui.config.OConfigManager;
import ru.obabok.oui.model.ModuleScreenFactory;
import ru.obabok.oui.model.OBaseScreen;
import ru.obabok.oui.screen.OUniversalSettingsScreen;

public class DefaultModuleScreenFactory implements ModuleScreenFactory {
    @Override
    public OBaseScreen create(Screen parent, Object moduleObject, String moduleName, OConfigManager<?> config) {
        return new OUniversalSettingsScreen(TranslationHelper.translateOrGet(moduleName), parent, moduleObject, moduleName, config);
    }
}
