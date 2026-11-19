package ru.obabok.oui.model;

public class OColor {
    public int color;

    public OColor(String color){
        this.color = getColor(color);
    }

    public OColor(int color){
        this.color = color;
    }

    public static int getColor(String argbStr) {
        if (argbStr == null || argbStr.length() != 8) {
            throw new IllegalArgumentException("Строка должна быть длиной ровно 8 символов (AARRGGBB)");
        }
        return (int) Long.parseLong(argbStr, 16);
    }

    public static String toHexString(int color) {
        return String.format("%08X", color);
    }

    @Override
    public String toString() {
        return toHexString(color);
    }
}
