package ru.obabok.narrator;

import net.fabricmc.api.ModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class NarratorModuleInit implements ModInitializer {
	public static NarratorConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("Narrator", NarratorConfig.class, Product.ID + ".config.module");
	}
}
