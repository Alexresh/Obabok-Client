package ru.obabok.blockupdates;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.OColor;

public class BlockUpdatesConfig implements OConfigInstance {

	@Module
	public BlockUpdatesModule BlockUpdates = new BlockUpdatesModule();

	public static class BlockUpdatesModule {
		@ConfigValue
		public boolean neighborUpdate = false;

		@ConfigValue
		public OColor neighborUpdateColor = new OColor("1C00FFFF");

		@ConfigValue
		public boolean shapeUpdate = false;

		@ConfigValue
		public OColor shapeUpdateColor = new OColor("1CF660FF");

		@ConfigValue
		public boolean packetUpdate = false;

		@ConfigValue
		public OColor packetUpdateColor = new OColor("88FFFFFF");

		@ConfigValue
		public int showTime = 5;
	}
}
