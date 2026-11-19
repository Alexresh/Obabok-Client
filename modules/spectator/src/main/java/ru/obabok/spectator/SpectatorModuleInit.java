package ru.obabok.spectator;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class SpectatorModuleInit implements ClientModInitializer {
	public static SpectatorConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("Spectator", SpectatorConfig.class, Product.ID + ".config.module");
	}
}
