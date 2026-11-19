package ru.obabok.oui.modConfig;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class OUIConfig implements OConfigInstance {
    @Module
    public OUIConfigModule OUIConfig = new OUIConfigModule();

    public static class OUIConfigModule {
        @ConfigValue
        public int maxScreenWidth = 380;

    }
}
