package ru.obabok.oui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.OuiClientInit;
import ru.obabok.oui.References;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;
import ru.obabok.oui.model.*;
import ru.obabok.oui.config.OConfig;
import ru.obabok.oui.config.OConfigManager;
import ru.obabok.oui.util.ConfigKeys;
import ru.obabok.oui.util.TranslationHelper;
import ru.obabok.oui.widget.button.OButtonWidget;
import ru.obabok.oui.widget.color.OColorWidget;
import ru.obabok.oui.widget.container.OContainerWidget;
import ru.obabok.oui.widget.container.OGridContainerWidget;
import ru.obabok.oui.widget.text.OFloatBoxWidget;
import ru.obabok.oui.widget.text.OIntBoxWidget;
import ru.obabok.oui.widget.text.OStringWidget;
import ru.obabok.oui.widget.text.OTextBoxWidget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OUniversalSettingsScreen extends OBaseScreen {
    private static final int ROW_HEIGHT = 20;
    private static final int RESET_BUTTON_WIDTH = 50;
    private static final int SPACING = 5;
    private static final int GRID_SPACING = 15;

    //filtering
    private OTextBoxWidget searchBox;
    private final Map<OBaseWidget, String> searchableWidgets = new HashMap<>();
    private final List<OBaseWidget> canvasWidgets = new ArrayList<>();
    OGridContainerWidget canvas;

    private final Screen parent;
    private final List<SettingSource> sources = new ArrayList<>();
    private final ArrayList<OValidatedTextBox<?>> needToBeValidated = new ArrayList<>();

    public OUniversalSettingsScreen(String title) {
        this((Component) TranslationHelper.translateOrGet(title));
    }

    public OUniversalSettingsScreen(String title, Screen parent) {
        this((Component) TranslationHelper.translateOrGet(title), parent);
    }

    public OUniversalSettingsScreen(String title, Screen parent, OConfigManager<?>... configs) {
        this((Component) TranslationHelper.translateOrGet(title), parent, configs);
    }

    public OUniversalSettingsScreen(String title, Screen parent, Object module, String modulePrefix, OConfigManager<?> config) {
        this((Component) TranslationHelper.translateOrGet(title), parent, module, modulePrefix, config);
    }

    public OUniversalSettingsScreen(Component title) {
        super(title);
        this.parent = null;
        addAllConfigs();
    }

    public OUniversalSettingsScreen(Component title, Screen parent, OConfigManager<?>... configs) {
        super(title);
        this.parent = parent;
        if (configs == null || configs.length == 0) {
            addAllConfigs();
        } else {
            for (OConfigManager<?> config : configs) {
                addConfig(config);
            }
        }
    }

    public OUniversalSettingsScreen(Component title, Screen parent, Object module, String modulePrefix, OConfigManager<?> config) {
        super(title);
        this.parent = parent;
        if (module != null) {
            SettingSource source = new SettingSource(module, modulePrefix, config);
            sources.add(source);
        }
    }

    private void addAllConfigs() {
        for (OConfigManager<?> config : OConfig.managers()) {
            addConfig(config);
        }
    }

    private void addConfig(OConfigManager<?> config) {
        if (config != null && config.getInstance() != null) {
            sources.add(new SettingSource(config.getInstance(), config.getKeyBase(), config));
        }
    }

    @Override
    protected void init() {
        super.init();

        searchableWidgets.clear();
        canvasWidgets.clear();

        needToBeValidated.clear();
        int canvasWidth = Math.clamp(width - 20, 220, OuiClientInit.config.OUIConfig.maxScreenWidth);

        OStringWidget titleWidget = new OStringWidget((width / 2) - ((canvasWidth - 40) / 2), 10, canvasWidth - 40, ROW_HEIGHT, title).withAlign(OStringWidget.Align.CENTER).withOutline(true);
        addRenderableWidget(titleWidget);
        if (parent != null) {
            addRenderableWidget(OButtonWidget.builder("<")
                    .pos(titleWidget.getX() - 19, 10)
                    .size(20, 20)
                    .onMouseClick(button -> onClose())
                    .build());
        }

        searchBox = new OTextBoxWidget(
                width / 2 - canvasWidth / 2,
                35,
                canvasWidth,
                20,
                Component.literal("Search..."));
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        canvas = new SettingsCanvas(width / 2 - canvasWidth / 2, 60, canvasWidth, height - 65, Component.literal(""));
        canvas.setCellSize(canvasWidth - 2 * GRID_SPACING, ROW_HEIGHT);

        for (SettingSource source : sources) {
            if (source.sourceObject == null) continue;
            for (Field field : source.sourceObject.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Module.class)) {
                    try {
                        addModuleButton(canvas, field, field.getAnnotation(Module.class), field.get(source.sourceObject), source);
                    } catch (IllegalAccessException e) {
                        References.LOGGER.error(e.getMessage());
                    }
                } else if (field.isAnnotationPresent(ConfigValue.class)) {
                    addConfigValueRow(canvas, source, field);
                }
            }
        }
        canvasWidgets.sort((w1, w2) -> {
            String s1 = searchableWidgets.getOrDefault(w1, "");
            String s2 = searchableWidgets.getOrDefault(w2, "");
            return s1.compareTo(s2);
        });
        onSearchChanged("");
        addRenderableWidget(canvas);
        setFocused(searchBox);
    }

    private void addModuleButton(OGridContainerWidget canvas, Field field, Module moduleAnnotation, Object moduleObject, SettingSource source) {
        String moduleKey = resolveModuleKey(field, moduleAnnotation, source.prefix);

        OBaseScreen moduleScreen = createModuleScreen(moduleAnnotation, moduleKey, moduleObject, source);

        OButtonWidget.Builder builder = OButtonWidget.builder(TranslationHelper.translateOrGet(moduleKey))
                .size(200, ROW_HEIGHT)
                .withTooltip(TranslationHelper.translateOrGet(moduleAnnotation.description()));
        if (moduleScreen != null) {
            builder.onMouseClick(_ -> minecraft.gui.setScreen(moduleScreen));
        }
        OButtonWidget moduleButton = builder.build();
        canvas.addWidget(moduleButton);
        canvasWidgets.add(moduleButton);
        searchableWidgets.put(moduleButton,
                TranslationHelper.translateOrGet(moduleKey).getString().toLowerCase() + " " +
                        TranslationHelper.translateOrGet(moduleAnnotation.description()).getString().toLowerCase());
    }

    private static String resolveModuleKey(Field field, Module moduleAnnotation, String parentPrefix) {
        if (!moduleAnnotation.name().isEmpty()) return moduleAnnotation.name();
        String auto = ConfigKeys.snakeCase(field.getName());
        return parentPrefix.isEmpty() ? auto : parentPrefix + "." + auto;
    }

    private OBaseScreen createModuleScreen(Module annotation, String moduleKey, Object moduleObject, SettingSource source) {
        try {
            ModuleScreenFactory factory = annotation
                    .screenFactory()
                    .getDeclaredConstructor()
                    .newInstance();

            return factory.create(this, moduleObject, moduleKey, source.config);
        } catch (ReflectiveOperationException e) {
            References.LOGGER.error(e.getMessage());
        }
        return null;
    }

    private void addConfigValueRow(OGridContainerWidget canvas, SettingSource source, Field configValueField) {
        ConfigValue annotation = configValueField.getAnnotation(ConfigValue.class);
        String effectiveName = annotation.value().isEmpty() ? ConfigKeys.snakeCase(configValueField.getName()) : annotation.value();
        String prefix = source.prefix;
        String configValueName;
        String configDescription;

        if (annotation.usePrefix() && !prefix.isEmpty()) {
            configValueName = prefix + "." + effectiveName;
            configDescription = annotation.description().isEmpty() ? "" : configValueName + "." + annotation.description();
        } else {
            configValueName = effectiveName;
            configDescription = annotation.description();
        }

        Class<?> configValueType = configValueField.getType();

        try {
            Object currentValue = configValueField.get(source.sourceObject);
            Object defaultValue = source.defaultObject == null ? null : configValueField.get(source.defaultObject);
            Component text = TranslationHelper.translateOrGet(configValueName);
            OStringWidget label = new OStringWidget(0, 0, font.width(text) + 10, ROW_HEIGHT, TranslationHelper.translateOrGet(configValueName))
                    .withTooltip(TranslationHelper.translateOrGet(configDescription)).withAlign(OStringWidget.Align.RIGHT);

            OBaseWidget control = null;

            if (configValueType == boolean.class) {
                control = OButtonWidget.builder(String.valueOf(currentValue))
                        .size(100, ROW_HEIGHT)
                        .withTooltip(Component.literal(""))
                        .onMouseClick(button -> {
                            try {
                                boolean newValue = !Boolean.parseBoolean(button.getMessage().getString());
                                configValueField.setBoolean(source.sourceObject, newValue);
                                save();
                                button.setMessage(Component.literal(String.valueOf(newValue)));
                            } catch (IllegalAccessException | IllegalArgumentException exception) {
                                References.LOGGER.error(exception.getMessage());
                            }
                        })
                        .build();
            } else if (configValueType == int.class) {
                OIntBoxWidget intWidget = new OIntBoxWidget(0, 0, 100, ROW_HEIGHT, Component.literal(""), (int) currentValue);
                Runnable set = () -> {
                    try {
                        if (intWidget.isTextValid(intWidget.getText())) {
                            configValueField.setInt(source.sourceObject, intWidget.getValue());
                            save();
                        } else {
                            setFocused(intWidget);
                            intWidget.selectAll();
                        }
                    } catch (IllegalAccessException | IllegalArgumentException exception) {
                        References.LOGGER.error(exception.getMessage());
                    }
                };
                intWidget.setOnEnter(set);
                intWidget.setFocusLost(set);
                needToBeValidated.add(intWidget);
                control = intWidget;
            } else if (configValueType == float.class) {
                OFloatBoxWidget floatWidget = new OFloatBoxWidget(0, 0, 100, ROW_HEIGHT, Component.literal(""), (float) currentValue);
                Runnable set = () -> {
                    try {
                        if (floatWidget.isTextValid(floatWidget.getText())) {
                            configValueField.setFloat(source.sourceObject, floatWidget.getValue());
                            save();
                        } else {
                            setFocused(floatWidget);
                            floatWidget.selectAll();
                        }
                    } catch (IllegalAccessException | IllegalArgumentException exception) {
                        References.LOGGER.error(exception.getMessage());
                    }
                };
                floatWidget.setOnEnter(set);
                floatWidget.setFocusLost(set);
                needToBeValidated.add(floatWidget);
                control = floatWidget;
            } else if (configValueType == OColor.class) {
                OColorWidget colorWidget = new OColorWidget(0, 0, 100, ROW_HEIGHT, ((OColor) currentValue).color);
                colorWidget.setOnChange(integer -> {
                    //colorWidget.setColor(integer);
                    try {
                        configValueField.set(source.sourceObject, new OColor(colorWidget.getColor()));
                    } catch (IllegalAccessException exception) {
                        References.LOGGER.error(exception.getMessage());
                    }
                });
                control = colorWidget;
            } else if (configValueType == String.class) {
                OTextBoxWidget textBoxWidget = new OTextBoxWidget(0, 0, 100, ROW_HEIGHT, Component.empty());
                textBoxWidget.setText((String) currentValue);
                Runnable set = () -> {
                    try {
                        if (textBoxWidget.isTextValid(textBoxWidget.getText())) {
                            configValueField.set(source.sourceObject, textBoxWidget.getText());
                            save();
                        } else {
                            setFocused(textBoxWidget);
                            textBoxWidget.selectAll();
                        }
                    } catch (IllegalAccessException | IllegalArgumentException exception) {
                        References.LOGGER.error(exception.getMessage());
                    }
                };
                textBoxWidget.setOnEnter(set);
                textBoxWidget.setFocusLost(set);
                control = textBoxWidget;
            }

            if (control != null) {
                SettingRow row = new SettingRow(label, control, createResetButton(source, configValueField, defaultValue, control));
                canvas.addWidget(row);
                canvasWidgets.add(row);
                searchableWidgets.put(row,
                        (configValueName + " " + configDescription + " " + TranslationHelper.translateOrGet(configValueName).getString()).toLowerCase());
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            References.LOGGER.error(e.getMessage());
        }
    }

    private void save() {
        OConfig.saveAll();
    }

    private void onSearchChanged(String query) {
        String q = query == null ? "" : query.toLowerCase().trim();

        List<OBaseWidget> currentWidgets = new ArrayList<>(canvas.getWidgets());
        for (OBaseWidget widget : currentWidgets) {
            canvas.removeWidger(widget);
        }

        for (OBaseWidget widget : canvasWidgets) {
            String searchTarget = searchableWidgets.get(widget);
            boolean visible = q.isEmpty() || (searchTarget != null && searchTarget.contains(q));

            if (visible) {
                canvas.addWidget(widget);
            }
        }
    }

    private OButtonWidget createResetButton(SettingSource source, Field configValueField, Object defaultValue, OBaseWidget control) {
        if (defaultValue == null) return null;
        return OButtonWidget.builder("Reset")
                .size(RESET_BUTTON_WIDTH, ROW_HEIGHT)
                .withTooltip(Component.literal("Reset to " + defaultValue))
                .onMouseClick(button -> {
                    try {
                        configValueField.set(source.sourceObject, defaultValue);
                        save();
                        if (control instanceof OTextBoxWidget textBox) {
                            textBox.setText(String.valueOf(defaultValue));
                        } else if (control instanceof OColorWidget colorWidget) {
                            colorWidget.setColor(((OColor)defaultValue).color);
                        } else {
                            control.setMessage(Component.literal(String.valueOf(defaultValue)));
                        }
                    } catch (IllegalAccessException | IllegalArgumentException exception) {
                        References.LOGGER.error(exception.getMessage());
                    }
                })
                .build();
    }

    private boolean validateAll() {
        boolean validated = true;
        for (OValidatedTextBox<?> textBox : needToBeValidated) {
            if (!textBox.isValueValid()) validated = false;
        }
        return validated;
    }

    @Override
    public void onClose() {
        if (validateAll()) {
            if (parent != null) {
                minecraft.gui.setScreen(parent);
            } else {
                Minecraft.getInstance().gui.setScreen(null);
            }
        } else {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WANDERING_TRADER_NO, 1.0F));
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
        //extractBlurredBackground(graphics);
    }

    private static class SettingsCanvas extends OGridContainerWidget {
        SettingsCanvas(int x, int y, int width, int height, Component message) {
            super(x, y, width, height, message);
        }

        @Override
        public boolean shouldTakeFocusAfterInteraction() {
            return false;
        }
    }

    private static class SettingSource {
        final Object sourceObject;
        final String prefix;
        final OConfigManager<?> config;
        final Object defaultObject;

        SettingSource(Object sourceObject, String prefix, OConfigManager<?> config) {
            this.sourceObject = sourceObject;
            this.prefix = prefix;
            this.config = config;

            Object freshInstance = null;
            try {
                freshInstance = sourceObject.getClass().getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                References.LOGGER.error("Не удалось создать дефолтный экземпляр настроек: " + e.getMessage());
            } catch (LinkageError | RuntimeException e) {
                References.LOGGER.error(e.getMessage());
            }
            this.defaultObject = freshInstance;
        }
    }

    public class SettingRow extends OContainerWidget {
        private final OBaseWidget label;
        private final OBaseWidget control;
        private final OBaseWidget reset;

        SettingRow(OBaseWidget label, OBaseWidget control, OBaseWidget reset) {
            super(0, 0, 100, ROW_HEIGHT, Component.literal(""));
            this.label = label;
            this.control = control;
            this.reset = reset;
            addWidget(label);
            if (control != null) addWidget(control);
            if (reset != null) addWidget(reset);
            setTooltip(null);
            relayout();
        }

        private void relayout() {
            int rowWidth = getWidth();
            int rowHeight = getHeight();

            label.setX(getX());
            label.setY(getY());
            label.setWidth(Math.max(0, rowWidth / 2 - SPACING));
            label.setHeight(rowHeight);

            if (control != null) {
                control.setX(getX() + rowWidth / 2);
                control.setY(getY());
                control.setWidth(Math.max(10, rowWidth / 2 - RESET_BUTTON_WIDTH - 2 * SPACING));
                control.setHeight(rowHeight);
            }

            if (reset != null) {
                reset.setX(getX() + rowWidth - RESET_BUTTON_WIDTH - SPACING);
                reset.setY(getY());
                reset.setWidth(Math.clamp(rowWidth, 10, RESET_BUTTON_WIDTH));
                reset.setHeight(rowHeight);
            }
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            relayout();
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            relayout();
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            relayout();
        }

        @Override
        public void setHeight(int height) {
            super.setHeight(height);
            relayout();
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            for (OBaseWidget widget : getWidgets()) {
                widget.extractRenderState(graphics, mouseX, mouseY, a);
            }
        }

        @Override
        public void onClick(MouseButtonEvent event, boolean doubleClick) {
            for (OBaseWidget widget : getWidgets()) {
                if (widget.isHovered()) {
                    if (widget instanceof OTextBoxWidget textBox) {
                        if (textBox.mouseClicked(event, doubleClick)) {
                            OUniversalSettingsScreen.this.setFocused(textBox);
                            setDragging(event.button() == 1);
                        }
                    } else {
                        widget.onClick(event, doubleClick);
                    }
                }
            }
        }
    }
}
