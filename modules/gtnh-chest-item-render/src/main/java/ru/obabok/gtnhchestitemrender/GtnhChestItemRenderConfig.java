package ru.obabok.gtnhchestitemrender;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class GtnhChestItemRenderConfig implements OConfigInstance {

	@Module
	public GtnhChestItemRenderModule GtnhChestItemRender = new GtnhChestItemRenderModule();

	public static class GtnhChestItemRenderModule {
		@ConfigValue
		public boolean enabled = false;

		@ConfigValue
		public float distance = 0.75f;

		@ConfigValue
		public float spacing = 0.2f;

		@ConfigValue
		public float scale = 0.2f;
	}
}
