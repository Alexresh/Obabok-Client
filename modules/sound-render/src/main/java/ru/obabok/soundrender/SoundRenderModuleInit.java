package ru.obabok.soundrender;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class SoundRenderModuleInit implements ClientModInitializer {
	public static SoundRenderConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("SoundRender", SoundRenderConfig.class, Product.ID + ".config.module");
	}
}
