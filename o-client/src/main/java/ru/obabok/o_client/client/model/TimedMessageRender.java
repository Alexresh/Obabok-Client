package ru.obabok.o_client.client.model;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TimedMessageRender {
    public int ticks = 60;
    public final Component message;
    public int color = 0xFFFFFFFF;
    public ItemStack item;

    public TimedMessageRender(Component message){
        this.message = message;
    }

    public TimedMessageRender(Component message, ItemStack itemStack){
        this.item = itemStack;
        this(message);
    }

    public TimedMessageRender withTicks(int ticks){
        this.ticks = ticks;
        return this;
    }

    public TimedMessageRender withColor(int color){
        this.color = color;
        return this;
    }

    public boolean hasItem(){
        return item != null;
    }
}
