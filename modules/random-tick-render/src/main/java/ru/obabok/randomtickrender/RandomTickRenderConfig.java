package ru.obabok.randomtickrender;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.OColor;

public class RandomTickRenderConfig implements OConfigInstance {

	@Module
	public RandomTickRenderModule RandomTickRender = new RandomTickRenderModule();

	public static class RandomTickRenderModule {
		@ConfigValue
		public boolean all = false;

		@ConfigValue
		public OColor allTickColor = new OColor("80FFFFFF");

		@ConfigValue
		public boolean blockState = false;

		@ConfigValue
		public OColor blockStateTickColor = new OColor("8000FF00");

		@ConfigValue
		public boolean fluidState = false;

		@ConfigValue
		public OColor fluidStateTickColor = new OColor("800000FF");

		@ConfigValue
		public int showTime = 1;
	}
}
