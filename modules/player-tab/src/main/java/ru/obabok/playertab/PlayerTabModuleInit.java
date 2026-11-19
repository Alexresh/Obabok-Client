package ru.obabok.playertab;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class PlayerTabModuleInit implements ClientModInitializer {
	public static PlayerTabConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("PlayerTab", PlayerTabConfig.class, Product.ID + ".config.module");
	}
}
