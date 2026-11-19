package ru.obabok.entityboundingbox;

import net.minecraft.client.Minecraft;
import ru.obabok.o_client.client.model.LevelRenderListener;
import ru.obabok.o_client.client.util.LevelRender;

public class EntityLevelRenderListener implements LevelRenderListener {
    @Override
    public void prepare(Minecraft minecraft) {
        if(EntityBoundingBoxModuleInit.config.EntityBoundingBox.enabled){
            minecraft.level.entitiesForRendering().forEach(entity -> {
                if(minecraft.player != null && !entity.is(minecraft.player)){
                    LevelRender.addCollisionBox(entity.getBoundingBox());
                }
            });
        }
    }

    @Override
    public void render() {

    }
}
