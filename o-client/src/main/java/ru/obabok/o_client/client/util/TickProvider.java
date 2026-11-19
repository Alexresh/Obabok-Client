package ru.obabok.o_client.client.util;

import net.minecraft.client.Minecraft;
import ru.obabok.o_client.References;
import ru.obabok.o_client.client.model.TickListener;

import java.util.ArrayList;
import java.util.List;

public class TickProvider {
    private static final List<TickListener> listeners = new ArrayList<>();

    public static void tick(Minecraft minecraft) {
        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onTick(minecraft);
            } catch (Exception e){
                References.LOGGER.error("[o-base][TickProvider] Error in module {}\n{}", listeners.get(i).getClass().getName(), e.getMessage());
            }
        }
    }

    public static void addListener(TickListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(TickListener listener) {
        listeners.remove(listener);
    }
}
