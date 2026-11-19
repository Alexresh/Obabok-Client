package ru.obabok.o_client.client.model;

import java.util.Objects;

public class TimedBlockRender {
    public final double x;
    public final double y;
    public final double z;
    public int ticks;
    public final int maxTicks;
    public final int color;
    public float size = 1f;
    public String label;
    public TimedBlockRender(double x, double y, double z, int ticksToExpire, int color) {
        this.x = x;
        this.y = y;
        this.z = z;
        maxTicks = ticksToExpire + 1;
        ticks = ticksToExpire + 1;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedBlockRender that = (TimedBlockRender) o;
        return x == that.x && y == that.y && z == that.z && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, color);
    }
}
