package ru.obabok.entitynametag.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Environment(EnvType.CLIENT)
@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    @Accessor("health")
    int o_client$getItemHealth();
}
