package ru.obabok.oui.widget.container;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;

import java.util.ArrayList;

public class OContainerWidget extends OBaseWidget {
    protected final ArrayList<OBaseWidget> widgets = new ArrayList<>();

    public OContainerWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractOutline(graphics, theme.decorations().outlineColor());
        graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        for (int i = 0; i < widgets.size(); i++) {
            widgets.get(i).extractRenderState(graphics, mouseX, mouseY, a);
        }
        graphics.disableScissor();
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        for (int i = 0; i < widgets.size(); i++) {
            if(widgets.get(i).isHovered()) widgets.get(i).onClick(event, doubleClick);
        }
    }

    public void addWidget(OBaseWidget widget){
        widgets.add(widget);
    }

    public void removeWidger(OBaseWidget widget){
        widgets.remove(widget);
    }

    public ArrayList<OBaseWidget> getWidgets() {
        return widgets;
    }

}
