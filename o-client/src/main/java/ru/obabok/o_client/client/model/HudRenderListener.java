package ru.obabok.o_client.client.model;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface HudRenderListener {
    int onRender(GuiGraphicsExtractor gui, int startX, int startY, int lineHeightAdd);
}
