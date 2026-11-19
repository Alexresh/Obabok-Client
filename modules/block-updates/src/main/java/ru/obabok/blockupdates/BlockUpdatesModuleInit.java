package ru.obabok.blockupdates;

import net.fabricmc.api.ModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class BlockUpdatesModuleInit implements ModInitializer {
	public static BlockUpdatesConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("BlockUpdates", BlockUpdatesConfig.class, Product.ID + ".config.module");
	}
}
