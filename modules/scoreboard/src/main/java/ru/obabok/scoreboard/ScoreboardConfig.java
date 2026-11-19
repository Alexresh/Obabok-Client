package ru.obabok.scoreboard;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ScoreboardConfig implements OConfigInstance {

	@Module
	public ScoreboardModule Scoreboard = new ScoreboardModule();

	public static class ScoreboardModule {
		@ConfigValue
		public float scoreboard_scale = 1.0f;
	}
}
