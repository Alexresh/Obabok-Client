package ru.obabok.oui.model;

public record Theme(Text text, Decorations decorations) {
    public record Text(int textColor, int selectionColor) {}
    public record Decorations(int outlineColor, int hoveredOutlineColor, int backgroundColor) {}
}