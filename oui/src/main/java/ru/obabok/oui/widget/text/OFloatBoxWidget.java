package ru.obabok.oui.widget.text;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import ru.obabok.oui.model.OValidatedTextBox;

import java.util.Optional;

public class OFloatBoxWidget extends OValidatedTextBox<Float> {
    private float min = -Float.MAX_VALUE;
    private float max = Float.MAX_VALUE;

    public OFloatBoxWidget(int x, int y, int width, int height, Component message, float defaultValue) {
        super(x, y, width, height, message, defaultValue);
        setTooltip(Tooltip.create(Component.literal("[" + min + ", " + max + "]")));
    }

    public void setBounds(float minValue, float maxValue){
        if(minValue <= maxValue){
            min = minValue;
            max = maxValue;
        } else {
            min = maxValue;
            max = minValue;
        }
        defaultValue = Mth.clamp(defaultValue, min, max);
    }

    @Override
    public Optional<Float> tryParse() {
        try {
            return Optional.of(Float.parseFloat(getText()));
        }catch (NumberFormatException exception){
            return Optional.empty();
        }
    }

    @Override
    protected boolean isCharAllowed(char charCode) {
        return (charCode == '-' && min < 0) || (charCode >= '0' && charCode <= '9') || (charCode == '.');
    }

    @Override
    public Float getValue() {
        Optional<Float> value = tryParse();
        return value.map(aFloat -> Mth.clamp(aFloat, min, max)).orElse(Mth.clamp(defaultValue, min, max));
    }
}
