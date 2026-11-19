package ru.obabok.oui.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.util.Themes;

public class OBaseScreen extends Screen {
    protected Theme theme = Themes.getCurrentTheme();

    public OBaseScreen(String title) {
        super(Component.literal(title));
    }

    public OBaseScreen(Component title){
        super(title);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(!super.mouseClicked(event, doubleClick)){
            clearFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
