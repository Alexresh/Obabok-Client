package ru.obabok.armorhud;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ArmorHudConfig implements OConfigInstance {

    @Module
    public ArmorHudModule ArmorHud = new ArmorHudModule();

    public static class ArmorHudModule {
        @ConfigValue
        public boolean armorDamage = false;

        @ConfigValue
        public boolean right = true;

        @ConfigValue
        public int offsetX = 10;

        @ConfigValue
        public int offsetY = -20;

        @ConfigValue
        public int spacing = 20;
    }
}
