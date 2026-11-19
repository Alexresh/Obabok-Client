package ru.obabok.oui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;

public class OButtonWidget extends OBaseWidget {
    protected final MouseClick onMouseClick;

    public static Builder builder(Component text){
        return new Builder(text);
    }

    public static Builder builder(String text){
        return new Builder(Component.literal(text));
    }


    protected OButtonWidget(int x, int y, int width, int height, Component text, MouseClick onMouseClick) {
        super(x, y, width, height, text);
        this.onMouseClick = onMouseClick;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        graphics.text(mc.font, message, getX() + (width / 2) - mc.font.width(message.getString()) / 2, getY() + (height / 2) - mc.font.lineHeight / 2, theme.text().textColor());
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        onMouseClick.onMouseClick(this);
    }

    public static class Builder {
        private int x;
        private int y;
        private int width = 100;
        private int height = 20;

        private final Component text;
        private Component tooltip;

        private MouseClick onClick = button -> {};

        Builder(Component text){
            this.text = text;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder withTooltip(Component tooltip){
            this.tooltip = tooltip;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder onMouseClick(MouseClick onClick){
            this.onClick = onClick;
            return this;
        }

        public OButtonWidget build() {
            OButtonWidget widget = new OButtonWidget(x, y, width, height, text, onClick);

            if(tooltip != null) widget.setTooltip(Tooltip.create(tooltip));

            return widget;
        }
    }

    public interface MouseClick {
        void onMouseClick(final OButtonWidget button);
    }

}
