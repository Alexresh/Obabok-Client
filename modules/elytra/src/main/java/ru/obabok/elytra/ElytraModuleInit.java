package ru.obabok.elytra;

import net.fabricmc.api.ModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ElytraModuleInit implements ModInitializer {
	public static ElytraConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("Elytra", ElytraConfig.class, Product.ID + ".config.module");
	}
}
