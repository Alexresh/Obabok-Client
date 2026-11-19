package ru.obabok.boat;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class BoatConfig implements OConfigInstance {

	@Module
	public BoatModule Boat = new BoatModule();

	public static class BoatModule {
		@ConfigValue
		public boolean fixedBoatRotation = false;
	}
}
