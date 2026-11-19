package ru.obabok.o_client.client.config;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.OColor;

public class OClientConfig implements OConfigInstance {
    @Module
    public LevelRenderModule LevelRender = new LevelRenderModule();

    @Module
    public HUDRenderModule hudRenderModule = new HUDRenderModule();

    public static class LevelRenderModule {
        @ConfigValue(usePrefix = false)
        public boolean enabled = false;

        @ConfigValue
        public OColor boundingBoxColor = new OColor(0x10FFFFFF);

    }

    public static class HUDRenderModule {
        @ConfigValue
        public boolean showHighlightBlocksCount = false;
    }

}
