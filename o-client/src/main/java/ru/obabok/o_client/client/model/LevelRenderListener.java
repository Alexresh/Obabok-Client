package ru.obabok.o_client.client.model;

import net.minecraft.client.Minecraft;

public interface LevelRenderListener {
    void prepare(Minecraft minecraft);
    void render();
}
