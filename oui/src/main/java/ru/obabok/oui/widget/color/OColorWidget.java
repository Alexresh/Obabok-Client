package ru.obabok.oui.widget.color;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;
import ru.obabok.oui.model.OColor;
import ru.obabok.oui.screen.OColorPickerScreen;

import java.util.function.Consumer;

public class OColorWidget extends OBaseWidget {
    private static final int ALPHA = 0xFF000000;

    private Consumer<Integer> onChange;
    private int color;

    public OColorWidget(int x, int y, int width, int height, int color) {
        this(x, y, width, height, color, null);
    }

    public OColorWidget(int x, int y, int width, int height, int color, Consumer<Integer> onChange) {
        super(x, y, width, height, Component.literal(OColor.toHexString(color)));
        this.color = color;
        this.onChange = onChange;
        setTooltip(Tooltip.create(Component.literal(OColor.toHexString(color))));
    }

    public int getColor() {
        return color;
    }

    public void setOnChange(Consumer<Integer> onChange) {
        this.onChange = onChange;
    }

    public void setColor(int color) {
        if (this.color == color) {
            return;
        }
        this.color = color;
        setMessage(Component.literal(OColor.toHexString(color)));
        setTooltip(Tooltip.create(Component.literal(OColor.toHexString(color))));
    }

    private void applyPickedColor(int pickedColor) {
        setColor(pickedColor);
        if (onChange != null) {
            onChange.accept(pickedColor);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        Screen parent = mc.gui.screen();
        mc.gui.setScreen(new OColorPickerScreen(parent, color, this::applyPickedColor));
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int left = getX() + 1;
        int top = getY() + 1;
        int right = getX() + width - 1;
        int bottom = getY() + height - 1;

        if ((color >>> 24) < ALPHA >>> 24) {
            drawCheckerboard(graphics, left, top, right, bottom);
        }
        graphics.fill(left, top, right, bottom, color);

        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

//        if (width >= 60) {
//            drawHexText(graphics, hexText());
//        }
    }

    private String hexText() {
        return OColor.toHexString(color);
    }

    private void drawHexText(GuiGraphicsExtractor graphics, String hex) {
        if (mc.font.width(hex) > width - 8) {
            return;
        }
        int textX = getX() + width / 2 - mc.font.width(hex) / 2;
        int textY = getY() + (height - mc.font.lineHeight) / 2;
        graphics.text(mc.font, hex, textX, textY, isLight() ? 0xFF000000 : 0xFFFFFFFF);
    }

    private boolean isLight() {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return (red * 299 + green * 587 + blue * 114) / 1000 > 127;
    }

    private void drawCheckerboard(GuiGraphicsExtractor graphics, int left, int top, int right, int bottom) {
        int cellSize = 8;
        int indexY = 0;
        for (int y = top; y < bottom; y += cellSize) {
            int indexX = 0;
            for (int x = left; x < right; x += cellSize) {
                int fill = (indexX + indexY) % 2 == 0 ? 0xFFC6C6C6 : 0xFF808080;
                graphics.fill(x, y, Math.min(x + cellSize, right), Math.min(y + cellSize, bottom), fill);
                indexX++;
            }
            indexY++;
        }
    }

}
