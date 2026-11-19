package ru.obabok.particlerender;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ParticleRenderConfig implements OConfigInstance {

	@Module
	public ParticleRenderModule ParticleRender = new ParticleRenderModule();

	public static class ParticleRenderModule {
		@ConfigValue
		public boolean boundingBox = false;

		@ConfigValue
		public boolean particleName = false;
	}
}
