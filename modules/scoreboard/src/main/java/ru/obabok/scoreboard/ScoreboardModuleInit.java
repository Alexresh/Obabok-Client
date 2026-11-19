package ru.obabok.scoreboard;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ScoreboardModuleInit implements ClientModInitializer {
	public static ScoreboardConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("Scoreboard", ScoreboardConfig.class, Product.ID + ".config.module");
	}
}
