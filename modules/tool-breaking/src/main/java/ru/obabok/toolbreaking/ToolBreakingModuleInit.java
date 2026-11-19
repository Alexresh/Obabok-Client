package ru.obabok.toolbreaking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.Product;

public class ToolBreakingModuleInit implements ClientModInitializer {
	public static ToolBreakingConfig config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("ToolBreaking", ToolBreakingConfig.class, Product.ID + ".config.module");

		ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
			if(config.ToolBreaking.disableToolBreaking){
				return player.getMainHandItem().isDamageableItem() && player.getMainHandItem().getDamageValue() >= player.getMainHandItem().getMaxDamage() - config.ToolBreaking.disableToolBreakingMinDurability;
			}
			return false;
		});
		UseItemCallback.EVENT.register((player, level, hand) -> {
			if(config.ToolBreaking.disableToolBreaking){
				if(player.getMainHandItem().isDamageableItem() && player.getMainHandItem().getDamageValue() >= player.getMainHandItem().getMaxDamage() - config.ToolBreaking.disableToolBreakingMinDurability) return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if(config.ToolBreaking.disableToolBreaking){
				if(player.getMainHandItem().isDamageableItem() && player.getMainHandItem().getDamageValue() >= player.getMainHandItem().getMaxDamage() - config.ToolBreaking.disableToolBreakingMinDurability) return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
	}
}
