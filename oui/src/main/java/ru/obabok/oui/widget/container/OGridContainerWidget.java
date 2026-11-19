package ru.obabok.oui.widget.container;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseWidget;

public class OGridContainerWidget extends OContainerWidget {

    private int cellWidth = 100;
    private int cellHeight = 25;
    private int bottomPanelHeight = 25;
    private int spacing = 15;


    private int currentPage = 0;
    private int itemsPerPage = 1;
    private int maxPages = 1;
    private int cols = 1;
    private int rows = 1;

    private static final int BTN_WIDTH = 20;
    private static final int BTN_HEIGHT = 20;

    private double scrollAccumulator = 0.0;

    public OGridContainerWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        recalculateLayout();
    }

    public void setCellSize(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        recalculateLayout();
    }

    @Override
    public void addWidget(OBaseWidget widget) {
        super.addWidget(widget);
        widget.setWidth(cellWidth);
        widget.setHeight(cellHeight);
        recalculateLayout();
    }

    @Override
    public void removeWidger(OBaseWidget widget) {
        super.removeWidger(widget);
        recalculateLayout();
    }

    public void recalculateLayout() {
        int availableWidth = getWidth();
        int availableHeight = getHeight() - bottomPanelHeight;

        this.cols = Math.max(1, availableWidth / (cellWidth + spacing));
        this.rows = Math.max(1, availableHeight / (cellHeight + spacing));

        itemsPerPage = cols * rows;

        maxPages = Math.max(1, (int) Math.ceil((double) widgets.size() / itemsPerPage));

        if (currentPage >= maxPages) {
            currentPage = maxPages - 1;
        }
        if (currentPage < 0) currentPage = 0;

        updateWidgetPositions();
    }

    private void updateWidgetPositions() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, widgets.size());

        for (int i = startIndex; i < endIndex; i++) {
            OBaseWidget widget = widgets.get(i);
            int localIndex = i - startIndex;

            int col = localIndex % this.cols;
            int row = localIndex / this.cols;

            widget.setX(getX() + spacing + col * (cellWidth + spacing));
            widget.setY(getY() + spacing + row * (cellHeight + spacing));
        }
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractOutline(graphics, theme.decorations().outlineColor());

        int gridBottomY = getY() + getHeight() - bottomPanelHeight;

        graphics.enableScissor(getX(), getY(), getX() + getWidth(), gridBottomY);

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, widgets.size());
        for (int i = startIndex; i < endIndex; i++) {
            widgets.get(i).extractRenderState(graphics, mouseX, mouseY, a);
        }

        graphics.disableScissor();

        drawBottomPanel(graphics, mouseX, mouseY);
    }

    private void drawBottomPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int panelY = getY() + getHeight() - bottomPanelHeight;
        int panelWidth = getWidth();

        //fill panel
        //graphics.fill(getX(), panelY, getX() + panelWidth, getY() + getHeight(), theme.outlineColor);

        int btnY = panelY + (bottomPanelHeight - BTN_HEIGHT) / 2;

        int prevBtnX = getX() + 5;
        boolean isPrevHovered = isMouseOver(mouseX, mouseY, prevBtnX, btnY, BTN_WIDTH, BTN_HEIGHT);
        graphics.text(mc.font, "<", prevBtnX + 6, btnY + 6, isPrevHovered ? theme.decorations().hoveredOutlineColor() : theme.decorations().outlineColor());


        int nextBtnX = getX() + panelWidth - BTN_WIDTH - 5;
        boolean isNextHovered = isMouseOver(mouseX, mouseY, nextBtnX, btnY, BTN_WIDTH, BTN_HEIGHT);
        graphics.text(mc.font, ">", nextBtnX + 6, btnY + 6, isNextHovered ? theme.decorations().hoveredOutlineColor() : theme.decorations().outlineColor());

        String pageText = "Page " + (currentPage + 1) + "/" + maxPages;
        int textWidth = mc.font.width(pageText);
        int textX = getX() + (panelWidth - textWidth) / 2;
        int textY = panelY + (bottomPanelHeight - mc.font.lineHeight) / 2;
        graphics.text(mc.font, pageText, textX, textY, theme.text().textColor());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        scrollAccumulator += scrollY;
        int pagesToScroll = (int) scrollAccumulator;
        if(pagesToScroll != 0)
        {
            scrollAccumulator -= pagesToScroll;

            if(pagesToScroll > 0){
                setPage(currentPage - 1);
                playDownSound(mc.getSoundManager());
            } else {
                setPage(currentPage + 1);
                playDownSound(mc.getSoundManager());
            }
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        int panelY = getY() + getHeight() - bottomPanelHeight;
        int btnY = panelY + (bottomPanelHeight - BTN_HEIGHT) / 2;

        int prevBtnX = getX() + 5;
        if (isMouseOver(mouseX, mouseY, prevBtnX, btnY, BTN_WIDTH, BTN_HEIGHT)) {
            setPage(currentPage - 1);
            return;
        }

        int nextBtnX = getX() + getWidth() - BTN_WIDTH - 5;
        if (isMouseOver(mouseX, mouseY, nextBtnX, btnY, BTN_WIDTH, BTN_HEIGHT)) {
            setPage(currentPage + 1);
            return;
        }

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, widgets.size());

        for (int i = startIndex; i < endIndex; i++) {
            OBaseWidget widget = widgets.get(i);
            if (widget.isHovered()) {
                widget.onClick(event, doubleClick);
                break;
            }
        }
    }

    public void setPage(int newPage){
        if(newPage >= 0 && newPage <= maxPages - 1){
            currentPage = newPage;
            updateWidgetPositions();
        }
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
