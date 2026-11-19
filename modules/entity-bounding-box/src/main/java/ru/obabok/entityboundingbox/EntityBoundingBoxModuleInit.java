package ru.obabok.entityboundingbox;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class EntityBoundingBoxModuleInit implements ClientModInitializer {
	public static EntityBoundingBoxConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("EntityBoundingBox", EntityBoundingBoxConfig.class, Product.ID + ".config.module");
		LevelRender.addListener(new EntityLevelRenderListener());
	}
}
