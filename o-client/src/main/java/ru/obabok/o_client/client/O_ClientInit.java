package ru.obabok.o_client.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.resources.Identifier;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.config.OClientConfig;
import ru.obabok.o_client.client.util.*;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class O_ClientInit implements ClientModInitializer {
	public static OClientConfig config;

	@Override
	public void onInitializeClient() {
		config = OConfig.register("OClient", OClientConfig.class, Product.ID + ".config.module");
		TickProvider.addListener(new HudRender());
		TickProvider.addListener(new LevelRender());

		ClientTickEvents.END_CLIENT_TICK.register(TickProvider::tick);

        LevelRenderEvents.END_MAIN.register(LevelRender::render);
        LevelRenderEvents.COLLECT_SUBMITS.register(SubmitsRender::collectSubmits);
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(References.MOD_ID, "hud"), HudRender::extract);
	}
}
