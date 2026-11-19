package ru.obabok.explosionrender;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ExplosionRenderModuleInit implements ClientModInitializer {
	public static ExplosionRenderConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("ExplosionRender", ExplosionRenderConfig.class, Product.ID + ".config.module");
	}
}
