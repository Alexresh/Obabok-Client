package ru.obabok.toolbreaking;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ToolBreakingConfig implements OConfigInstance {

	@Module
	public ToolBreakingModule ToolBreaking = new ToolBreakingModule();

	public static class ToolBreakingModule {
		@ConfigValue
		public boolean disableToolBreaking = false;

		@ConfigValue
		public int disableToolBreakingMinDurability = 30;
	}
}
