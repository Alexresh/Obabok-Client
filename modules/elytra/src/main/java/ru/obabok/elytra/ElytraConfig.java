package ru.obabok.elytra;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ElytraConfig implements OConfigInstance {

	@Module
	public ElytraModule Elytra = new ElytraModule();

	public static class ElytraModule {
		@ConfigValue
		public boolean allowWaterTakeoff = false;

		@ConfigValue
		public boolean allowLavaTakeoff = false;
	}
}
