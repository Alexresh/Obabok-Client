package ru.obabok.playertab;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class PlayerTabConfig implements OConfigInstance {

	@Module
	public PlayerTabModule PlayerTab = new PlayerTabModule();

	public static class PlayerTabModule {
		@ConfigValue
		public boolean tabInfo = false;

		@ConfigValue
		public float tabScale = 1.0f;
	}
}
