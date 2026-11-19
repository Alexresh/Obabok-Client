package ru.obabok.randomtickrender;

import net.fabricmc.api.ModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class RandomTickRenderModuleInit implements ModInitializer {
	public static RandomTickRenderConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("RandomTickRender", RandomTickRenderConfig.class, Product.ID + ".config.module");
	}
}
