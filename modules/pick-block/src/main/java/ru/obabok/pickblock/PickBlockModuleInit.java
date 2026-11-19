package ru.obabok.pickblock;

import net.fabricmc.api.ModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class PickBlockModuleInit implements ModInitializer {
	public static PickBlockConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("PickBlock", PickBlockConfig.class, Product.ID + ".config.module");
	}
}
