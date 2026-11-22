package ru.obabok.lagdetector;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class LagDetectorConfig implements OConfigInstance {

    @Module
    public LagDetectorModule LagDetector = new LagDetectorModule();

    public static class LagDetectorModule {
        @ConfigValue
        public boolean lagDetectorEnabled = false;

        @ConfigValue
        public int threshold = 1500;
    }
}
