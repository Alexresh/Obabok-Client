package ru.obabok.oui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import ru.obabok.oui.modConfig.OUIConfig;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class OuiClientInit implements ClientModInitializer {
    public static OUIConfig config;
    @Override
    public void onInitializeClient() {
        config = OConfig.register("oui", OUIConfig.class, Product.ID + ".config.module");
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> OConfig.saveAll());
    }
}
