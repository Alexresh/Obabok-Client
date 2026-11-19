package ru.obabok.lagdetector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.CommonColors;
import ru.obabok.o_client.client.model.HudRenderListener;

public class LagDetectorHud implements HudRenderListener {
    private Font font;

    @Override
    public int onRender(GuiGraphicsExtractor gui, int startX, int startY, int lineHeightAdd) {
        if(font == null) font = Minecraft.getInstance().font;
        if(LagDetectorModuleInit.config.LagDetector.lagDetectorEnabled && LagDetectorModuleInit.isLagging()){
            String lag = "LAG " + (int)(LagDetectorModuleInit.getTimeSinceLastPacket() / 1000.0);
            gui.fill(gui.guiWidth() / 2 - (font.width(lag) / 2) - 5, 15, gui.guiWidth() / 2 + (font.width(lag) / 2) + 5, 20 + font.lineHeight + 5, 0x80FF0000);
            gui.text(font, lag, gui.guiWidth() / 2 - (font.width(lag) / 2), 20, CommonColors.RED);
        }
        return startY;
    }
}
