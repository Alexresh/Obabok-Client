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
import net.minecraft.world.phys.Vec3;
import ru.obabok.o_client.client.model.SubmitsListener;

import java.util.ArrayList;

public class NametagSubmitsListener implements SubmitsListener {
    @Override
    public void collectSubmits(LevelRenderContext context, PoseStack poses, SubmitNodeCollector collector, Vec3 cameraPos, CameraRenderState camera) {
        if(!EntityNametagModuleInit.config.EntityNametag.enabled) return;
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null) return;

        minecraft.level.entitiesForRendering().forEach(entity -> {
            if(entity instanceof LivingEntity livingEntity && minecraft.player != null && !entity.is(minecraft.player)){
                ArrayList<String> lines = new ArrayList<>();
                if(entity.getCustomName() != null){
                    //nametag.append(entity.getCustomName().getString());
                    lines.add(entity.getCustomName().getString());
                }
                if(EntityNametagModuleInit.config.EntityNametag.health){
                    //nametag.append(" ").append(livingEntity.getHealth()).append("/").append(livingEntity.getMaxHealth());
                    lines.add(livingEntity.getHealth() + "/" + livingEntity.getMaxHealth());
                }
                if(entity instanceof TropicalFish tropicalFish && EntityNametagModuleInit.config.EntityNametag.tropicalFishInfo){
                    //nametag.append(" ").append(tropicalFish.getBaseColor().getSerializedName());
                    //nametag.append(" ").append(tropicalFish.getPattern().getSerializedName());
                    //nametag.append(" ").append(tropicalFish.getPatternColor().getSerializedName());
                    lines.add(tropicalFish.getBaseColor().getSerializedName() + " "
                            + tropicalFish.getPattern().getSerializedName() + " "
                            + tropicalFish.getPatternColor().getSerializedName());
                }
                if(entity instanceof AbstractHorse horse && EntityNametagModuleInit.config.EntityNametag.horseInfo){
                    //nametag.append(" JS").append(horse.getAttributeValue(Attributes.JUMP_STRENGTH));
                    //nametag.append(" H").append(horse.getAttributeValue(Attributes.MAX_HEALTH));
                    //nametag.append(" S").append(horse.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    lines.add("Jump strength: " + String.format("%.2f", horse.getAttributeValue(Attributes.JUMP_STRENGTH)));
                    lines.add("Max health: " + String.format("%.2f", horse.getAttributeValue(Attributes.MAX_HEALTH)));
                    lines.add("Movement speed: " + String.format("%.2f", horse.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
                for (int i = 0; i < lines.size(); i++) {
                    collector.submitNameTag(
                            poses,
                            new Vec3(entity.getX() - cameraPos.x, entity.getBoundingBox().maxY + 0.1 - cameraPos.y, entity.getZ() - cameraPos.z),
                            i * -10,
                            Component.literal(lines.get(i)),
                            true,
                            0xF000F0,
                            camera
                    );
                }
            }
        });
    }
}
