package ru.obabok.entitynametag;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.o_client.client.util.SubmitsRender;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class EntityNametagModuleInit implements ClientModInitializer {
	public static EntityNametagConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("EntityNametag", EntityNametagConfig.class, Product.ID + ".config.module");
		SubmitsRender.addListener(new NametagSubmitsListener());
	}
}
