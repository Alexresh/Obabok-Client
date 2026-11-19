package ru.obabok.screenclassnames;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ScreenClassNamesModuleInit implements ClientModInitializer {
	public static ScreenClassNamesConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("ScreenClassNames", ScreenClassNamesConfig.class, Product.ID + ".config.module");
	}
}
