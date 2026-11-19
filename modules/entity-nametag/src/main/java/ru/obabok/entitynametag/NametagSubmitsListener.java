package ru.obabok.entitynametag;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import ru.obabok.entitynametag.mixin.ItemEntityAccessor;
import ru.obabok.o_client.client.model.SubmitsListener;

import java.util.ArrayList;

public class NametagSubmitsListener implements SubmitsListener {
    @Override
    public void collectSubmits(LevelRenderContext context, PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera) {
        if(!EntityNametagModuleInit.config.EntityNametag.enabled) return;
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null) return;

        minecraft.level.entitiesForRendering().forEach(entity -> {
            ArrayList<Component> lines = new ArrayList<>();
            if(entity instanceof LivingEntity livingEntity && minecraft.player != null && entity != minecraft.player){

                if(entity.getCustomName() != null){
                    lines.add(entity.getCustomName());
                }
                if(EntityNametagModuleInit.config.EntityNametag.health){
                    lines.add(Component.literal(livingEntity.getHealth() + "/" + livingEntity.getMaxHealth()));
                }
                if(entity instanceof TropicalFish tropicalFish && EntityNametagModuleInit.config.EntityNametag.tropicalFishInfo){
                    lines.add(Component.literal(tropicalFish.getBaseColor().getSerializedName() + " "
                            + tropicalFish.getPattern().getSerializedName() + " "
                            + tropicalFish.getPatternColor().getSerializedName()));
                }
                if(entity instanceof AbstractHorse horse && EntityNametagModuleInit.config.EntityNametag.horseInfo){
                    lines.add(Component.literal("Jump: " + fmt(horse.getAttributeValue(Attributes.JUMP_STRENGTH))));
                    lines.add(Component.literal("Max health:  " + fmt(horse.getAttributeValue(Attributes.MAX_HEALTH))));
                    lines.add(Component.literal("Movement speed: " + fmt(horse.getAttributeValue(Attributes.MOVEMENT_SPEED))));
                }
            } else if (EntityNametagModuleInit.config.EntityNametag.itemInfo && entity instanceof ItemEntity itemEntity) {
                if(EntityNametagModuleInit.config.EntityNametag.itemHealth) lines.add(Component.literal("H:" + ((ItemEntityAccessor)itemEntity).o_client$getItemHealth()));
                lines.add(itemEntity.getItem().getHoverName().copy().append(" x" + itemEntity.getItem().getCount()));
            }

            Vec3 p = entity.getPosition(context.levelState().worldPartialTicks);
            double y = p.y + entity.getBbHeight() + 0.1;

            for (int i = 0; i < lines.size(); i++) {
                collector.submitNameTag(
                        poses,
                        new Vec3(p.x - cameraPos.x, y - cameraPos.y, p.z - cameraPos.z),
                        -i * 10,
                        lines.get(i),
                        true,
                        0xF000F0,
                        camera
                );
            }
        });
    }

    private static String fmt(double value) {
        long scaled = Math.round(Math.abs(value) * 100.0);
        long whole = scaled / 100;
        long frac  = scaled % 100;
        return (value < 0 ? "-" : "") + whole + "." + (frac < 10 ? "0" : "") + frac;
    }
}
