package ru.obabok.lagdetector;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.o_client.client.util.HudRender;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class LagDetectorModuleInit implements ClientModInitializer {
	public static LagDetectorConfig config;
	@Override
	public void onInitializeClient() {
		HudRender.addHudRenderListener(new LagDetectorHud());
		config = OConfig.register("LagDetector", LagDetectorConfig.class, Product.ID + ".config.module");
	}

	private static long lastPacketTime = System.currentTimeMillis();

	public static void onPacketReceived() {
		lastPacketTime = System.currentTimeMillis();
	}

	public static long getTimeSinceLastPacket() {
		return System.currentTimeMillis() - lastPacketTime;
	}

	public static boolean isLagging() {
		return getTimeSinceLastPacket() > config.LagDetector.threshold;
	}
}
