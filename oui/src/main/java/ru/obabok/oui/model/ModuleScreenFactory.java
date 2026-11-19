package ru.obabok.oui.model;

import net.minecraft.client.gui.screens.Screen;
import ru.obabok.oui.config.OConfigManager;

public interface ModuleScreenFactory {

    OBaseScreen create(
            Screen parent,
            Object moduleObject,
            String moduleName,
            OConfigManager<?> config
    );
}
