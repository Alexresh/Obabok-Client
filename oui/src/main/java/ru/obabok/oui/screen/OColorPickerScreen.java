package ru.obabok.oui.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseScreen;
import ru.obabok.oui.model.OBaseWidget;
import ru.obabok.oui.model.OColor;
import ru.obabok.oui.widget.button.OButtonWidget;
import ru.obabok.oui.widget.text.OStringWidget;
import ru.obabok.oui.widget.text.OTextBoxWidget;

import java.util.function.Consumer;

public class OColorPickerScreen extends OBaseScreen {
    private static final int SPACING = 8;
    private static final int SV_WIDTH = 170;
    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 16;
    private static final int CELL_SIZE = 6;

    private final Screen parent;
    private final Consumer<Integer> onChange;
    private final int initialColor;
    private final boolean[] initialApplied = {false};

    private int color_alpha;
    private float hue;
    private float saturation;
    private float brightness;

    private OTextBoxWidget hexBox;

    private static int color;

    public OColorPickerScreen(Screen parent, int initialColor, Consumer<Integer> onChange) {
        super(Component.literal("Color"));
        this.parent = parent;
        this.onChange = onChange;
        this.initialColor = initialColor;
        color = initialColor;
        updateHsvFromColor(initialColor);
    }

    @Override
    protected void init() {
        super.init();

        int columnWidth = SV_WIDTH + BAR_WIDTH + SPACING;
        int columnX = width / 2 - columnWidth / 2;
        int planeY = 40;
        int planeHeight = Math.max(60, Math.min(140, height - planeY - SPACING - BAR_HEIGHT - SPACING * 3 - 20 - 10));

        OStringWidget titleWidget = new OStringWidget((width / 2) - 290 / 2, 10, 290, 20, title).withAlign(OStringWidget.Align.CENTER);
        addRenderableWidget(titleWidget);

        if (parent != null) {
            addRenderableWidget(OButtonWidget.builder("<")
                    .pos(titleWidget.getX() - 25, 10)
                    .size(20, 20)
                    .onMouseClick(button ->{
                        applyHexText();
                        onClose();
                    })
                    .build());
        }

        addRenderableWidget(new SaturationValueArea(columnX, planeY, SV_WIDTH, planeHeight));
        addRenderableWidget(new HueBar(columnX + SV_WIDTH + SPACING, planeY, BAR_WIDTH, planeHeight));
        addRenderableWidget(new AlphaBar(columnX, planeY + planeHeight + SPACING, columnWidth, BAR_HEIGHT));

        int bottomY = planeY + planeHeight + SPACING + BAR_HEIGHT + SPACING * 2;

        addRenderableWidget(new OldNewPreview(columnX, bottomY, 50, 20, CommonColors.WHITE));

        hexBox = new OTextBoxWidget(columnX + 58, bottomY, 80, 20, Component.literal(""));
        hexBox.setMaxLength(8);
        hexBox.setTextValidator(s -> s.matches("(?i)[0-9a-f]{0,8}"));
        hexBox.setSuggestion(Component.literal("aarrggbb"));
        hexBox.setOnEnter(this::applyHexText);
        hexBox.setFocusLost(this::applyHexText);
        syncHexBox();
        addRenderableWidget(hexBox);

        addRenderableWidget(OButtonWidget.builder("Ok")
                .pos(columnX + 148, bottomY)
                .size(46, 20)
                .onMouseClick(button -> {
                    applyHexText();
                    onClose();
                })
                .build());

        if (!initialApplied[0]) {
            initialApplied[0] = true;
            notifyChange(initialColor);
        }
    }

    private void applyHexText() {
        String text = hexBox.getText();
        if (text.length() == 8) {
            try {
                int parsed = (int) Long.parseLong(text, 16);
                color = parsed;
                updateHsvFromColor(color);
            } catch (NumberFormatException ignored) {
            }
        }
        syncHexBox();
    }

    private void syncHexBox() {
        if (hexBox != null) {
            hexBox.setText(OColor.toHexString(color));
        }
    }

    private void updateColorFromHsv() {
        int rgb = hsvToRgb(hue, saturation, brightness);
        color = (color_alpha << 24) | (rgb & 0xFFFFFF);
        syncHexBox();
        notifyChange(color);
    }

    private void updateColorFromAlpha(float alphaFraction) {
        color_alpha = Math.round(Mth.clamp(alphaFraction, 0.0F, 1.0F) * 255.0F);
        color = (color_alpha << 24) | (color & 0xFFFFFF);
        syncHexBox();
        notifyChange(color);
    }

    private void updateHsvFromColor(int newColor) {
        color_alpha = (newColor >>> 24) & 0xFF;
        float[] hsv = rgbToHsv(newColor);
        hue = hsv[0];
        saturation = hsv[1];
        brightness = hsv[2];
    }

    private void notifyChange(int newColor) {
        if (onChange != null) {
            onChange.accept(newColor);
        }
    }

    @Override
    public void onClose() {
        notifyChange(color);
        if (parent != null) {
            minecraft.gui.setScreen(parent);
        } else {
            super.onClose();
        }
    }

    private static class OldNewPreview extends OBaseWidget {
        private final int oldColor;

        OldNewPreview(int x, int y, int width, int height, int oldColor) {
            super(x, y, width, height, Component.literal("Old / New"));
            this.oldColor = oldColor;
            setTooltip(null);
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            int half = width / 2;
            graphics.fill(getX() + 1, getY() + 1, getX() + half - 1, getY() + height - 1, oldColor);
            graphics.fill(getX() + half + 1, getY() + 1, getX() + width - 1, getY() + height - 1, color);
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        }
    }

    private class SaturationValueArea extends OBaseWidget {
        SaturationValueArea(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Saturation / Brightness"));
            setTooltip(null);
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
            int columns = Math.max(2, width / CELL_SIZE);
            int rows = Math.max(2, height / CELL_SIZE);
            float cellWidth = (float) width / columns;
            float cellHeight = (float) height / rows;

            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    float s = (column + 0.5F) / columns;
                    float v = (row + 0.5F) / rows;
                    int fill = 0xFF000000 | (hsvToRgb(hue, s, 1.0F - v) & 0xFFFFFF);
                    int cellX = getX() + Math.round(column * cellWidth);
                    int cellY = getY() + Math.round(row * cellHeight);
                    int cellEndX = getX() + Math.round((column + 1) * cellWidth);
                    int cellEndY = getY() + Math.round((row + 1) * cellHeight);
                    graphics.fill(cellX, cellY, cellEndX, cellEndY, fill);
                }
            }

            drawMarker(graphics,
                    getX() + Math.round(saturation * (width - 1)),
                    getY() + Math.round((1.0F - brightness) * (height - 1)));
        }

        private void updateFromMouse(MouseButtonEvent event) {
            if (width <= 1 || height <= 1) {
                return;
            }
            saturation = Mth.clamp((float) (event.x() - getX()) / (width - 1), 0.0F, 1.0F);
            brightness = Mth.clamp(1.0F - (float) (event.y() - getY()) / (height - 1), 0.0F, 1.0F);
            updateColorFromHsv();
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.active || !this.visible || event.button() != 1) {
                return false;
            }
            if (isMouseOver(event.x(), event.y())) {
                playDownSound(mc.getSoundManager());
                updateFromMouse(event);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            updateFromMouse(event);
            return true;
        }
    }

    private class HueBar extends OBaseWidget {
        HueBar(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Hue"));
            setTooltip(null);
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
            int steps = Math.max(2, height / CELL_SIZE);
            for (int step = 0; step < steps; step++) {
                float stepHue = step / (float) (steps - 1);
                int fill = 0xFF000000 | (hsvToRgb(1.0F - stepHue, 1.0F, 1.0F) & 0xFFFFFF);
                int cellY = getY() + Math.round(step * (float) height / steps);
                int cellEndY = getY() + Math.round((step + 1) * (float) height / steps);
                graphics.fill(getX() + 1, cellY, getX() + width - 1, cellEndY, fill);
            }
            drawMarker(graphics,
                    getX() + width / 2,
                    getY() + Math.round((1.0F - hue) * (height - 1)));
        }

        private void updateFromMouse(MouseButtonEvent event) {
            if (height <= 1) {
                return;
            }
            hue = Mth.clamp(1.0F - (float) (event.y() - getY()) / (height - 1), 0.0F, 1.0F);
            updateColorFromHsv();
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.active || !this.visible || event.button() != 1) {
                return false;
            }
            if (isMouseOver(event.x(), event.y())) {
                playDownSound(mc.getSoundManager());
                updateFromMouse(event);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            updateFromMouse(event);
            return true;
        }
    }

    private class AlphaBar extends OBaseWidget {
        AlphaBar(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Alpha"));
            setTooltip(null);
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

            int left = getX() + 1;
            int right = getX() + width - 1;
            for (int y = getY() + 1; y < getY() + height - 1; y += 6) {
                int index = 0;
                for (int x = left; x < right; x += 6) {
                    int fill = index % 2 == 0 ? 0xFFC6C6C6 : 0xFF808080;
                    graphics.fill(x, y, Math.min(x + 6, right), Math.min(y + 6, getY() + height - 1), fill);
                    index++;
                }
            }

            int rgb = color & 0xFFFFFF;
            int steps = Math.max(2, width / CELL_SIZE);
            for (int step = 0; step < steps; step++) {
                float stepFraction = step / (float) (steps - 1);
                int fill = (Math.round(stepFraction * 255.0F) << 24) | rgb;
                int cellX = getX() + 1 + Math.round(step * (float) (width - 2) / steps);
                int cellEndX = getX() + 1 + Math.round((step + 1) * (float) (width - 2) / steps);
                graphics.fill(cellX, getY() + 1, cellEndX, getY() + height - 1, fill);
            }

            int markerX = getX() + 1 + Math.round(color_alpha / 255.0F * (width - 2));
            graphics.fill(markerX - 1, getY() + 1, markerX + 2, getY() + height - 1, 0xFFFFFFFF);
            graphics.fill(markerX, getY() + 1, markerX + 1, getY() + height - 1, 0xFF000000);

        }

        private void updateFromMouse(MouseButtonEvent event) {
            if (width <= 2) {
                return;
            }
            updateColorFromAlpha((float) (event.x() - (getX() + 1)) / (width - 2));
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.active || !this.visible || event.button() != 1) {
                return false;
            }
            if (isMouseOver(event.x(), event.y())) {
                playDownSound(mc.getSoundManager());
                updateFromMouse(event);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            updateFromMouse(event);
            return true;
        }
    }

    private static void drawMarker(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x - 3, y - 3, x + 3, y + 3, 0xFFFFFFFF);
        graphics.fill(x - 2, y - 2, x + 2, y + 2, 0xFF000000);
        graphics.fill(x - 1, y - 1, x + 1, y + 1, 0xFFFFFFFF);
    }

    private static int hsvToRgb(float hue, float saturation, float brightness) {
        hue = (hue % 1.0F + 1.0F) % 1.0F;
        brightness = Mth.clamp(brightness, 0.0F, 1.0F);
        saturation = Mth.clamp(saturation, 0.0F, 1.0F);

        int sector = (int) (hue * 6.0F);
        float fraction = hue * 6.0F - sector;
        float p = brightness * (1.0F - saturation);
        float q = brightness * (1.0F - fraction * saturation);
        float t = brightness * (1.0F - (1.0F - fraction) * saturation);

        float red;
        float green;
        float blue;
        switch (sector % 6) {
            case 0: red = brightness; green = t; blue = p; break;
            case 1: red = q; green = brightness; blue = p; break;
            case 2: red = p; green = brightness; blue = t; break;
            case 3: red = p; green = q; blue = brightness; break;
            case 4: red = t; green = p; blue = brightness; break;
            default: red = brightness; green = p; blue = q; break;
        }

        return (Math.round(red * 255.0F) << 16) | (Math.round(green * 255.0F) << 8) | Math.round(blue * 255.0F);
    }

    private static float[] rgbToHsv(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        float max = Math.max(red, Math.max(green, blue)) / 255.0F;
        float min = Math.min(red, Math.min(green, blue)) / 255.0F;
        float delta = max - min;

        float hue = 0.0F;
        if (delta > 0.0F) {
            if (max == red / 255.0F) {
                hue = (green - blue) / 255.0F / delta + (green < blue ? 6.0F : 0.0F);
            } else if (max == green / 255.0F) {
                hue = (blue - red) / 255.0F / delta + 2.0F;
            } else {
                hue = (red - green) / 255.0F / delta + 4.0F;
            }
            hue /= 6.0F;
        }
        float saturation = max <= 0.0F ? 0.0F : delta / max;
        return new float[]{hue, saturation, max};
    }
}
