package ru.obabok.entityboundingbox;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class EntityBoundingBoxConfig implements OConfigInstance {

	@Module
	public EntityBoundingBoxModule EntityBoundingBox = new EntityBoundingBoxModule();

	public static class EntityBoundingBoxModule {
		@ConfigValue
		public boolean enabled = false;
	}
}
