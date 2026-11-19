package ru.obabok.soundrender;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.OColor;

public class SoundRenderConfig implements OConfigInstance {

	@Module
	public SoundRenderModule SoundRender = new SoundRenderModule();

	public static class SoundRenderModule {
		@ConfigValue(usePrefix = false)
		public boolean enabled = false;

		@ConfigValue
		public int soundRenderTicks = 5;

		@ConfigValue
		public OColor soundEventColor = new OColor("80FFFF00");

		@ConfigValue
		public float soundEventSize = 0.2f;
	}
}
