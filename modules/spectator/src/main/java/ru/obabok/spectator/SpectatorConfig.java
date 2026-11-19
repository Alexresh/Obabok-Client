package ru.obabok.spectator;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class SpectatorConfig implements OConfigInstance {

	@Module
	public SpectatorModule Spectator = new SpectatorModule();

	public static class SpectatorModule {
		@ConfigValue
		public boolean newSpectatorScreen = false;
	}
}
