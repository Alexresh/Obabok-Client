package ru.obabok.oui.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.util.Themes;

import java.time.Duration;

public class OBaseWidget extends AbstractWidget {
    protected Theme theme = Themes.getCurrentTheme();
    protected Minecraft mc = Minecraft.getInstance();

    public OBaseWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        setTooltip(Tooltip.create(message));
        setTooltipDelay(Duration.ofMillis(500));
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractOutline(graphics, isHovered ? theme.decorations().hoveredOutlineColor() : theme.decorations().outlineColor());
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {

    }

    protected void extractOutline(GuiGraphicsExtractor graphics, int color){
        int x = getX();
        int y = getY();

        graphics.fill(x + 2, y, x + width - 2, y + 1, color);
        graphics.fill(x + 2, y + height - 1, x + width - 2, y + height, color);
        graphics.fill(x, y + 2, x + 1, y + height - 2, color);
        graphics.fill(x + width - 1, y + 2, x + width, y + height - 2, color);

        graphics.fill(x, y, x + 1, y + 1 , color);
        graphics.fill(x + width - 1, y, x + width, y + 1 , color);
        graphics.fill(x, y + height - 1, x + 1, y + height , color);
        graphics.fill(x + width - 1, y + height - 1, x + width, y + height , color);
    }
}
