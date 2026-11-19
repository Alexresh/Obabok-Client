package ru.obabok.oui.widget.text;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.lwjgl.sdl.SDLKeycode;
import org.lwjgl.sdl.SDLScancode;
import ru.obabok.oui.model.OBaseWidget;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OTextBoxWidget extends OBaseWidget {
    protected String text = "";
    protected int maxLength = 32;

    protected int displayPos = 0;
    protected int cursorPos = 0;
    protected int selectionPos = 0;

    protected boolean editable = true;
    protected boolean drawSuggestion = true;
    protected Component suggestion;

    protected Predicate<String> textValidator = s -> true;
    protected Consumer<String> responder;
    protected Runnable enterHandler;
    protected Runnable focusLost;

    public OTextBoxWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        if (newText == null) {
            newText = "";
        }

        String filtered = filterInsertion("", "", newText);

        if (filtered.equals(this.text)) {
            this.cursorPos = Mth.clamp(this.cursorPos, 0, this.text.length());
            this.selectionPos = this.cursorPos;
            ensureCursorVisible();
            return;
        }

        this.text = filtered;
        this.cursorPos = this.text.length();
        this.selectionPos = this.cursorPos;

        ensureCursorVisible();
        onTextChanged();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);

        if (this.text.length() > this.maxLength) {
            setText(this.text.substring(0, this.maxLength));
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setSuggestion(Component suggestion) {
        this.suggestion = suggestion;
    }

    public void setDrawSuggestion(boolean drawSuggestion) {
        this.drawSuggestion = drawSuggestion;
    }

    public void setTextValidator(Predicate<String> validator) {
        this.textValidator = validator == null ? s -> true : validator;
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void setOnEnter(Runnable enterHandler) {
        this.enterHandler = enterHandler;
    }

    public void setFocusLost(Runnable focusLost){
        this.focusLost = focusLost;
    }

    public boolean isTextValid(String value) {
        return value != null && value.length() <= maxLength && textValidator.test(value);
    }

    protected boolean isCharAllowed(char charCode) {
        return !Character.isISOControl(charCode);
    }

    protected String filterInsertion(String before, String after, String insertion) {
        StringBuilder sb = new StringBuilder();

        for (char c : insertion.toCharArray()) {
            if (!isCharAllowed(c)) {
                continue;
            }

            String candidate = before + sb + c + after;

            if (isTextValid(candidate)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public void insertText(String textToInsert) {
        if (!editable || textToInsert == null || textToInsert.isEmpty()) {
            return;
        }

        int start = Math.min(cursorPos, selectionPos);
        int end = Math.max(cursorPos, selectionPos);

        String before = text.substring(0, start);
        String after = text.substring(end);

        String filtered = filterInsertion(before, after, textToInsert);

        if (filtered.isEmpty()) {
            return;
        }

        String newText = before + filtered + after;

        if (!isTextValid(newText)) {
            return;
        }

        this.text = newText;
        this.cursorPos = start + filtered.length();
        this.selectionPos = this.cursorPos;

        ensureCursorVisible();
        onTextChanged();
    }

    public void deleteChars(int direction) {
        if (!editable) {
            return;
        }

        if (hasSelection()) {
            deleteSelection();
            return;
        }

        int start = cursorPos;
        int end = cursorPos;

        if (direction < 0) {
            start = Math.max(0, cursorPos - 1);
        } else {
            end = Math.min(text.length(), cursorPos + 1);
        }

        if (start == end) {
            return;
        }

        String before = text.substring(0, start);
        String after = text.substring(end);

        this.text = before + after;
        this.cursorPos = start;
        this.selectionPos = cursorPos;

        ensureCursorVisible();
        onTextChanged();
    }

    public void deleteSelection() {
        if (!editable || !hasSelection()) {
            return;
        }

        int start = Math.min(cursorPos, selectionPos);
        int end = Math.max(cursorPos, selectionPos);

        String before = text.substring(0, start);
        String after = text.substring(end);

        this.text = before + after;
        this.cursorPos = start;
        this.selectionPos = cursorPos;

        ensureCursorVisible();
        onTextChanged();
    }

    public boolean hasSelection() {
        return cursorPos != selectionPos;
    }

    public String getSelectedText() {
        int start = Math.min(cursorPos, selectionPos);
        int end = Math.max(cursorPos, selectionPos);
        return text.substring(start, end);
    }

    public void selectAll() {
        if (text.isEmpty()) {
            selectionPos = 0;
            cursorPos = 0;
        } else {
            selectionPos = 0;
            cursorPos = text.length();
        }
    }

    public void moveCursor(int delta, boolean shift) {
        setCursorPos(cursorPos + delta, shift);
    }

    public void setCursorPos(int pos, boolean shift) {
        int oldCursor = cursorPos;
        cursorPos = Mth.clamp(pos, 0, text.length());

        if (!shift) {
            selectionPos = cursorPos;
        } else if (selectionPos == oldCursor && oldCursor != cursorPos) {
            selectionPos = oldCursor;
        }

        ensureCursorVisible();
    }

    protected void copy() {
        if (hasSelection()) {
            setClipboard(getSelectedText());
        }
    }

    protected void cut() {
        if (!editable || !hasSelection()) {
            return;
        }

        copy();
        deleteSelection();
    }

    protected void paste() {
        if (!editable) {
            return;
        }

        String clipboard = getClipboard();

        if (clipboard != null && !clipboard.isEmpty()) {
            insertText(clipboard);
        }
    }

    protected String getClipboard() {
        return mc.keyboardHandler.getClipboard();
    }

    protected void setClipboard(String value) {
        mc.keyboardHandler.setClipboard(value);
    }

    @Override
    public boolean capturesInput() {
        return this.visible && this.isFocused();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!isFocused() || !editable) {
            return false;
        }

        if (!event.isAllowedChatCharacter()) {
            return false;
        }

        insertText(event.codepointAsString());
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused()) {
            return false;
        }
        int keyCode = event.keycode();

        boolean shift = (event.modifiers() != 0 && InputConstants.isKeyDown(SDLScancode.SDL_SCANCODE_LSHIFT));
        boolean ctrl = (event.modifiers() != 0 && InputConstants.isKeyDown(SDLScancode.SDL_SCANCODE_LCTRL));

        if (keyCode == SDLKeycode.SDLK_ESCAPE) {
            setFocused(false);
            return true;
        }

        if (keyCode == SDLKeycode.SDLK_RETURN) {
            if (enterHandler != null) {
                enterHandler.run();
            } else {
                setFocused(false);
            }
            return true;
        }

        if (keyCode == SDLKeycode.SDLK_TAB) {
            return false;
        }

        switch (keyCode) {
            case SDLKeycode.SDLK_BACKSPACE:
                deleteChars(-1);
                return true;

            case SDLKeycode.SDLK_DELETE:
                deleteChars(1);
                return true;

            case SDLKeycode.SDLK_LEFT:
                moveCursor(-1, shift);
                return true;

            case SDLKeycode.SDLK_RIGHT:
                moveCursor(1, shift);
                return true;

            case SDLKeycode.SDLK_HOME:
                setCursorPos(0, shift);
                return true;

            case SDLKeycode.SDLK_END:
                setCursorPos(text.length(), shift);
                return true;

            case SDLKeycode.SDLK_A:
                if (ctrl) {
                    selectAll();
                    return true;
                }
                return false;

            case SDLKeycode.SDLK_C:
                if (ctrl) {
                    copy();
                    return true;
                }
                return false;

            case SDLKeycode.SDLK_X:
                if (ctrl) {
                    cut();
                    return true;
                }
                return false;

            case SDLKeycode.SDLK_V:
                if (ctrl) {
                    paste();
                    return true;
                }
                return false;

            default:
                return false;
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        super.onClick(event, doubleClick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.active || !this.visible) {
            return false;
        }

        if (event.button() != 1) {
            return false;
        }

        setFocused(true);

        boolean shift = mc.hasShiftDown();
        int index = getCharIndexAt(event.x());

        if (!shift) {
            selectionPos = cursorPos;
        }

        setCursorPos(index, shift);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (!this.active || !this.visible || !isFocused() || event.button() != 1) {
            return false;
        }

        if (isMouseOver(event.x(), event.y())) {
            setCursorPos(getCharIndexAt(event.x()), true);
            return true;
        }

        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        boolean wasFocused = isFocused();
        super.setFocused(focused);

        if (wasFocused && !focused) {
            onFocusLost();
        }
        Minecraft.getInstance().onTextInputFocusChange(this, focused);
    }

    protected void onFocusLost() {
        if (focusLost != null) {
            focusLost.run();
        }
    }

    protected void onTextChanged() {
        ensureCursorVisible();

        if (responder != null) {
            responder.accept(text);
        }
    }

    @Override
    public void setMessage(Component message) {
        setText(message.getString());
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!this.visible) {
            return;
        }

        graphics.fill(
                getX() + 1,
                getY() + 1,
                getX() + width - 1,
                getY() + height - 1,
                theme.decorations().backgroundColor()
        );

        renderContents(graphics);

        // Рисуем аутлайн из твоего OBaseWidget
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
    }


    protected void renderContents(GuiGraphicsExtractor graphics) {
        int textX = getX() + 4;
        int textY = getY() + Math.max(0, (height - 8) / 2);

        String visibleText = getVisibleText();

        int selStart = Math.min(cursorPos, selectionPos);
        int selEnd = Math.max(cursorPos, selectionPos);

        int selectionTop = getY() + 4;
        int selectionBottom = getY() + height - 4;

        if (selectionBottom <= selectionTop) {
            selectionBottom = selectionTop + 1;
        }

        if (isFocused() && selStart != selEnd) {
            int startX = getXForIndex(selStart, textX, visibleText);
            int endX = getXForIndex(selEnd, textX, visibleText);

            int minX = getX() + 4;
            int maxX = getX() + width - 4;

            startX = Mth.clamp(startX, minX, maxX);
            endX = Mth.clamp(endX, minX, maxX);

            if (endX > startX) {
                graphics.fill(startX, selectionTop, endX, selectionBottom, theme.text().selectionColor());
            }
        }

        if (visibleText.isEmpty() && !isFocused() && drawSuggestion && suggestion != null) {
            graphics.text(mc.font, suggestion, textX, textY, theme.text().textColor());
        } else {
            graphics.text(
                    mc.font,
                    visibleText,
                    textX,
                    textY,
                    editable ? theme.text().textColor() : theme.decorations().outlineColor()
            );
        }

        if (isFocused() && editable && shouldDrawCursor()) {
            int cursorX = getXForIndex(cursorPos, textX, visibleText);
            cursorX = Mth.clamp(cursorX, getX() + 4, getX() + width - 4);

            graphics.fill(
                    cursorX,
                    selectionTop,
                    cursorX + 1,
                    selectionBottom,
                    theme.text().textColor()
            );
        }
    }

    protected String getVisibleText() {
        int maxWidth = Math.max(0, width - 8);

        if (maxWidth <= 0 || displayPos >= text.length()) {
            return "";
        }

        return mc.font.plainSubstrByWidth(text.substring(displayPos), maxWidth);
    }

    protected int getXForIndex(int index, int textX, String visibleText) {
        if (index <= displayPos) {
            return textX;
        }

        int endIndex = Math.min(index, displayPos + visibleText.length());

        if (endIndex <= displayPos) {
            return textX;
        }

        return textX + mc.font.width(text.substring(displayPos, endIndex));
    }

    protected int getCharIndexAt(double mouseX) {
        int textX = getX() + 4;
        int maxWidth = Math.max(0, width - 8);

        if (maxWidth <= 0 || mouseX <= textX) {
            return displayPos;
        }

        String visibleText = getVisibleText();
        int rightBound = textX + maxWidth;

        if (mouseX >= rightBound) {
            return Mth.clamp(displayPos + visibleText.length(), 0, text.length());
        }

        String sub = mc.font.plainSubstrByWidth(visibleText, (int) (mouseX - textX));
        return Mth.clamp(displayPos + sub.length(), 0, text.length());
    }

    protected void ensureCursorVisible() {
        int maxWidth = Math.max(0, width - 8);

        if (displayPos > text.length()) {
            displayPos = text.length();
        }

        if (mc.font.width(text) <= maxWidth) {
            displayPos = 0;
            return;
        }

        if (cursorPos < displayPos) {
            displayPos = cursorPos;
        }

        while (displayPos < cursorPos && mc.font.width(text.substring(displayPos, cursorPos)) > maxWidth) {
            displayPos++;
        }
    }

    protected boolean shouldDrawCursor() {
        return (Util.getMillis() / 300) % 2 == 0;
    }
}
