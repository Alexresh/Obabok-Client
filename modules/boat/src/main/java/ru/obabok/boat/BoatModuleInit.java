package ru.obabok.boat;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class BoatModuleInit implements ClientModInitializer {
	public static BoatConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("Boat", BoatConfig.class, Product.ID + ".config.module");
	}
}
