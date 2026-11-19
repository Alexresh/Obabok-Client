package ru.obabok.oui.widget.text;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;

public class OStringWidget extends OBaseWidget {

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    private boolean outline = false;
    private Align align = Align.LEFT;

    public OStringWidget(int x, int y, int width, int height, String message) {
        super(x, y, width, height, Component.literal(message));
    }

    public OStringWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int textWidth = mc.font.width(message);
        int posX = 5;
        switch (align){
            case LEFT -> posX = 5;
            case CENTER -> posX = width / 2 - textWidth / 2;
            case RIGHT -> posX = width - textWidth;
        }
        graphics.text(mc.font, message, getX() + posX, getY() + height / 2 - mc.font.lineHeight / 2, theme.text().textColor());
        if(outline){
            extractOutline(graphics, theme.decorations().outlineColor());
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    public OStringWidget withAlign(Align align){
        setAlign(align);
        return this;
    }

    public OStringWidget withTooltip(Component tooltip){
        setTooltip(Tooltip.create(tooltip));
        return this;
    }

    public OStringWidget withOutline(boolean withOutline){
        setOutline(withOutline);
        return this;
    }
}
