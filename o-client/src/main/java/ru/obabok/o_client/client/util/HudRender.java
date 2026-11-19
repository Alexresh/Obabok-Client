package ru.obabok.o_client.client.util;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.CommonColors;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.O_ClientInit;
import ru.obabok.o_client.client.model.HudRenderListener;
import ru.obabok.o_client.client.model.TickListener;
import ru.obabok.o_client.client.model.TimedMessageRender;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HudRender implements TickListener {
    private static final Queue<TimedMessageRender> pendingAdds = new ConcurrentLinkedQueue<>();
    private static final List<TimedMessageRender> messages = new ArrayList<>();
    private static final List<HudRenderListener> listeners = new ArrayList<>();
    private static final int hudPosX = 5;
    private static final int lineHeightAdd =  10;

    public static void extract(GuiGraphicsExtractor gui, DeltaTracker deltaTracker) {
        int y = 5;
        int highlightCount = LevelRender.getHighlightCount();
        if(highlightCount != 0 && O_ClientInit.config.hudRenderModule.showHighlightBlocksCount){
            gui.text(Minecraft.getInstance().font, "renders: " + highlightCount, hudPosX, y, CommonColors.WHITE);
            y += Minecraft.getInstance().font.lineHeight;
        }

        Iterator<TimedMessageRender> iterator = messages.iterator();
        while (iterator.hasNext()) {
            TimedMessageRender message = iterator.next();
            if (message.ticks <= 0) {
                iterator.remove();
            }

            boolean hasItem = message.hasItem();

            gui.text(Minecraft.getInstance().font, message.message, hudPosX + (hasItem ? 27 : 4), y + 4, message.color, false);
            gui.outline(hudPosX, y, Minecraft.getInstance().font.width(message.message) + (hasItem ? 30 : 7), Minecraft.getInstance().font.lineHeight + lineHeightAdd, message.color);
            if (hasItem) {
                gui.item(message.item, hudPosX + 3, y + 1);
            }
            y += Minecraft.getInstance().font.lineHeight + lineHeightAdd * 2;
        }

        renderModules(gui, hudPosX, y, lineHeightAdd);
    }

    public static void addMessage(TimedMessageRender message){
        if(!pendingAdds.contains(message))
            pendingAdds.add(message);
    }

    public static void addHudRenderListener(HudRenderListener listener) {
        listeners.add(listener);
    }

    public static void removeHudRenderListener(HudRenderListener listener) {
        listeners.remove(listener);
    }

    private static void renderModules(GuiGraphicsExtractor gui, int startX, int startY, int lineHeightAdd){
        int newY = startY;
        for (int i = 0; i < listeners.size(); i++) {
            try {
                newY = listeners.get(i).onRender(gui, startX, newY, lineHeightAdd);
            } catch (Exception e){
                References.LOGGER.error("[o-base][HudRender] Error in module {}\n{}", listeners.get(i).getClass().getName(), e.getMessage());
            }
        }
    }

    @Override
    public void onTick(Minecraft minecraft) {
        TimedMessageRender item;
        while ((item = pendingAdds.poll()) != null) {
            messages.add(item);
        }

        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).ticks--;
        }
    }
}
