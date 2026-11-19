package ru.obabok.oui.model;

import net.minecraft.network.chat.Component;
import ru.obabok.oui.widget.text.OTextBoxWidget;

import java.util.Optional;

public abstract class OValidatedTextBox <T> extends OTextBoxWidget {
    protected T defaultValue;

    public OValidatedTextBox(int x, int y, int width, int height, Component message, T defaultValue) {
        super(x, y, width, height, message);
        this.defaultValue = defaultValue;
        setText(String.valueOf(defaultValue));
    }

    public abstract Optional<T> tryParse();

    public T getValue() {
        return tryParse().orElse(defaultValue);
    }

    public boolean isValueValid() {
        return tryParse().isPresent();
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }
}
