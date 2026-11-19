package ru.obabok.pickblock;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class PickBlockConfig implements OConfigInstance {

	@Module
	public PickBlockModule PickBlock = new PickBlockModule();

	public static class PickBlockModule {
		@ConfigValue
		public boolean copyBlockState = false;
	}
}
