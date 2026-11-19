package ru.obabok.particlerender;

import net.fabricmc.api.ModInitializer;
import ru.obabok.o_client.client.util.LevelRender;
import ru.obabok.o_client.client.util.SubmitsRender;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ParticleRenderModuleInit implements ModInitializer {
	public static ParticleRenderConfig config;

	@Override
	public void onInitialize() {
		config = OConfig.register("ParticleRender", ParticleRenderConfig.class, Product.ID + ".config.module");
		LevelRender.addListener(new ParticleLevelRenderListener());
		SubmitsRender.addListener(new ParticleSubmitsListener());
	}
}
