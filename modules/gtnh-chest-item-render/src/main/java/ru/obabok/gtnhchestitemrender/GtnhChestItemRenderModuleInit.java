package ru.obabok.gtnhchestitemrender;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.o_client.client.util.SubmitsRender;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class GtnhChestItemRenderModuleInit implements ClientModInitializer {
	public static GtnhChestItemRenderConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("GtnhChestItemRender", GtnhChestItemRenderConfig.class, Product.ID + ".config.module");
		SubmitsRender.addListener(new ChestItemSubmitsListener());
	}
}
