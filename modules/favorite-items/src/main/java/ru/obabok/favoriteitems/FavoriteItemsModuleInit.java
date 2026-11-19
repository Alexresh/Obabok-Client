package ru.obabok.favoriteitems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class FavoriteItemsModuleInit implements ClientModInitializer {
	public static FavoriteItemsConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("FavoriteItems", FavoriteItemsConfig.class, Product.ID + ".config.module");
		ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> FavoriteItemsStorage.init(client));
	}
}
