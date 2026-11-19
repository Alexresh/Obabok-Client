package ru.obabok.oui.widget.container;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;

import java.util.ArrayList;
import java.util.Arrays;

public class OLinesContainerWidget extends OContainerWidget{
    public ArrayList<ArrayList<OBaseWidget>> lines = new ArrayList<>();
    private int spacing = 5;

    public OLinesContainerWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

    }

    public int getMaxWidth(){
        int maxWidth = 0;
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            int lineWidth = spacing;
            for (int widgetIndex = 0; widgetIndex < lines.get(lineIndex).size(); widgetIndex++) {
                lineWidth += lines.get(lineIndex).get(widgetIndex).getWidth() + spacing;
            }
            if(maxWidth < lineWidth) maxWidth = lineWidth;
        }
        return maxWidth;
    }

    public void addEmptyLine(){
        lines.add(new ArrayList<>());
    }

    public void addLine(ArrayList<OBaseWidget> widgets){
        lines.add(widgets);
    }

    public void addWidgetOnLine(int line, OBaseWidget widget){
        if(line < 0 || line >= lines.size()) return;
        lines.get(line).add(widget);
    }

}
