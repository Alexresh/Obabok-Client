package ru.obabok.o_client.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import ru.obabok.oui.model.OBaseScreen;
import ru.obabok.oui.widget.button.OButtonWidget;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;

public class ObjectInspectorScreen extends Screen {
    private final Deque<Object> history = new ArrayDeque<>();
    private InspectorList list;
    public ObjectInspectorScreen(Object rootObject) {
        super(Component.literal("a"));
        if (rootObject != null) {
            history.push(rootObject);
        }
    }

    @Override
    protected void init() {
        int listHeight = this.height - 60;
        int listTopY = 40;
        int itemHeight = 24;

        list = new InspectorList(this.minecraft, this.width, listHeight, listTopY, itemHeight);
        this.addRenderableWidget(list);

        this.addRenderableWidget(Button.builder(Component.literal("← Назад"), button -> goBack())
                .bounds(10, 10, 80, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("✕ Закрыть"), button -> this.onClose())
                .bounds(100, 10, 80, 20)
                .build());

        refreshList();
    }

    private void refreshList() {
        list.clear();

        if (history.isEmpty()) return;

        Object currentObject = history.peek();

        if (currentObject == null) {
            list.addEntry(new FieldEntry("null", "null", null, false));
            return;
        }

        Class<?> clazz = currentObject.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;


                try {
                    field.setAccessible(true);
                    Object value = field.get(currentObject);
                    String typeName = field.getType().getSimpleName();
                    boolean canDrillDown = canNavigateInto(value);

                    list.addEntry(new FieldEntry(field.getName(), typeName, value, canDrillDown));
                } catch (InaccessibleObjectException e) {
                    list.addEntry(new FieldEntry(field.getName(), field.getType().getSimpleName(),
                            "§c[Закрыто модулем Java]", false));
                } catch (IllegalAccessException e) {
                    list.addEntry(new FieldEntry(field.getName(), field.getType().getSimpleName(),
                            "§c[Нет доступа]", false));
                }
            }
            clazz = clazz.getSuperclass();
        }
        list.setScrollAmount(0);
    }

    private boolean canNavigateInto(Object value) {
        if (value == null) return false;
        Class<?> type = value.getClass();
        if (type.isPrimitive() || isWrapperType(type)) return false;
        if (value instanceof String) return false;
        return true;
    }

    private boolean isWrapperType(Class<?> type) {
        return type == Boolean.class || type == Byte.class || type == Character.class ||
                type == Short.class || type == Integer.class || type == Long.class ||
                type == Float.class || type == Double.class;
    }

    private void goBack() {
        if (history.size() > 1) {
            history.pop();
            refreshList();
        } else {
            this.onClose();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    private class InspectorList extends ObjectSelectionList<FieldEntry> {

        public InspectorList(Minecraft minecraft, int screenWidth, int height, int y, int itemHeight) {
            super(minecraft, screenWidth, height, y, itemHeight);
        }

        public void clear() {
            this.clearEntries();
        }

        @Override
        public int getRowLeft() {
            return getX();
        }

        @Override
        public int getRowWidth() {
            return width - 20;
        }

        @Override
        public int addEntry(FieldEntry entry) {
            return super.addEntry(entry);
        }
    }

    private class FieldEntry extends ObjectSelectionList.Entry<FieldEntry> {
        private final String name;
        private final String type;
        private final Object value;
        private final boolean canDrillDown;

        public FieldEntry(String name, String type, Object value, boolean canDrillDown) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.canDrillDown = canDrillDown;
            this.setWidth(width);

        }

        @Override
        public Component getNarration() {
            return Component.literal(name + ": " + getValueString());
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (event.button() == 1 && canDrillDown) {
                history.push(value);
                refreshList();
                return true;
            }
            return false;
        }

        private String getValueString() {
            if (value == null) return "§7null§r";

            String str = value.toString();
            if (str.length() > 100) {
                str = str.substring(0, 97) + "...";
            }
            str = str.replace("\n", "\\n").replace("\r", "\\r");

            return canDrillDown ? "§b[" + str + "]§r" : str;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {

            String displayText = String.format("§e%s §7[%s]§r = %s", name, type, getValueString());

            int color = 0xFFFFFFFF;

            if (canDrillDown && hovered) {
                color = 0xFF00FF00; // Ярко-зелёный при наведении, чтобы точно бросался в глаза
                displayText = "§a→ " + displayText;
            }

            graphics.text(minecraft.font, displayText, getX() + 4, getY() + 4, color, false);
        }
    }

}
