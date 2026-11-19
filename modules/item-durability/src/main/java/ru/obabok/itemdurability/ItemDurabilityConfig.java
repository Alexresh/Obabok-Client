package ru.obabok.itemdurability;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ItemDurabilityConfig implements OConfigInstance {

	@Module
	public ItemDurabilityModule ItemDurability = new ItemDurabilityModule();

	public static class ItemDurabilityModule {
		@ConfigValue
		public boolean enabled = false;
	}
}
