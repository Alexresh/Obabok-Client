package ru.obabok.itemdurability;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ItemDurabilityModuleInit implements ClientModInitializer {
	public static ItemDurabilityConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("ItemDurability", ItemDurabilityConfig.class, Product.ID + ".config.module");
	}
}
