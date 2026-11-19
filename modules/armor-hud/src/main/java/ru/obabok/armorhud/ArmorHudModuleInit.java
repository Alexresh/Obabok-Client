package ru.obabok.armorhud;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ArmorHudModuleInit implements ClientModInitializer {
    public static ArmorHudConfig config;
	@Override
	public void onInitializeClient() {
    	config = OConfig.register("ArmorHud", ArmorHudConfig.class, Product.ID + ".config.module");
	}
}
