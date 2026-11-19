package ru.obabok.screenclassnames;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ScreenClassNamesConfig implements OConfigInstance {

	@Module
	public ScreenClassNamesModule ScreenClassNames = new ScreenClassNamesModule();

	public static class ScreenClassNamesModule {
		@ConfigValue
		public boolean enabled = false;
	}
}
