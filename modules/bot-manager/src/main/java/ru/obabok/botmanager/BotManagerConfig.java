package ru.obabok.botmanager;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class BotManagerConfig implements OConfigInstance {

	@Module
	public BotManagerModule BotManager = new BotManagerModule();

	public static class BotManagerModule {
		@ConfigValue
		public String botTeam = "";
	}
}
