package ru.obabok.explosionrender;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.OColor;

public class ExplosionRenderConfig implements OConfigInstance {

	@Module
	public ExplosionRenderModule ExplosionRender = new ExplosionRenderModule();

	public static class ExplosionRenderModule {
		@ConfigValue
		public boolean enabled = false;

		@ConfigValue
		public float size = 0.2f;

		@ConfigValue
		public int ticks = 60;

		@ConfigValue
		public OColor color = new OColor("A0FFFF00");
	}
}
