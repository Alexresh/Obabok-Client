package ru.obabok.narrator;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class NarratorConfig implements OConfigInstance {

	@Module
	public NarratorModule Narrator = new NarratorModule();

	public static class NarratorModule {
		@ConfigValue
		public boolean suppressNarratorWarning = false;
	}
}
