package ru.obabok.botmanager;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import ru.obabok.o_client.client.model.TickListener;
import ru.obabok.o_client.client.util.TickProvider;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class BotManagerModuleInit implements ClientModInitializer, TickListener {
	public static BotManagerConfig config;

	KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(Product.ID, "bot_manager")
	);
	KeyMapping openBotManagerKey = KeyMappingHelper.registerKeyMapping(
			new KeyMapping(
					"key.bot_manager.open_screen",
					InputConstants.Type.KEYBOARD,
					InputConstants.UNKNOWN.getValue(),
					this.CATEGORY
			));

	@Override
	public void onInitializeClient() {
		config = OConfig.register("BotManager", BotManagerConfig.class, Product.ID + ".config.module");
		TickProvider.addListener(this);
	}

	@Override
	public void onTick(Minecraft minecraft) {
		if(openBotManagerKey.consumeClick()){
			minecraft.gui.setScreen(new BotManagerScreen());
		}
	}
}
