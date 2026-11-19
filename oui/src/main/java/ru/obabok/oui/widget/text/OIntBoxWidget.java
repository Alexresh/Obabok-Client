package ru.obabok.oui.widget.text;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import ru.obabok.oui.model.OValidatedTextBox;

import java.util.Optional;

public class OIntBoxWidget extends OValidatedTextBox<Integer> {
    protected int max = Integer.MAX_VALUE;
    protected int min = Integer.MIN_VALUE;

    public OIntBoxWidget(int x, int y, int width, int height, Component message, int defaultValue) {
        super(x, y, width, height, message, defaultValue);
        setMaxLength(16);
        setText(String.valueOf(this.defaultValue));
    }

    public void setBounds(int minValue, int maxValue){
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
    protected boolean isCharAllowed(char charCode) {
        return (charCode == '-' && min < 0) || (charCode >= '0' && charCode <= '9');
    }

    @Override
    public Optional<Integer> tryParse() {
        try {
            return Optional.of(Integer.parseInt(getText()));
        }catch (NumberFormatException exception){
            return Optional.empty();
        }
    }

    @Override
    public Integer getValue() {
        Optional<Integer> value = tryParse();
        return value.map(integer -> Mth.clamp(integer, min, max)).orElse(Mth.clamp(defaultValue, min, max));
    }
}
