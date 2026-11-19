package ru.obabok.entitynametag;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class EntityNametagConfig implements OConfigInstance {

	@Module
	public EntityNametagModule EntityNametag = new EntityNametagModule();

	public static class EntityNametagModule {
		@ConfigValue
		public boolean enabled = false;

		@ConfigValue
		public boolean health = false;

		@ConfigValue
		public boolean tropicalFishInfo = false;

		@ConfigValue
		public boolean horseInfo = false;
	}
}
