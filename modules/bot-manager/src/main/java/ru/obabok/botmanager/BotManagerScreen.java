package ru.obabok.botmanager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.NonNull;
import ru.obabok.oui.model.OBaseScreen;
import ru.obabok.oui.widget.button.OButtonWidget;
import ru.obabok.oui.widget.text.OStringWidget;
import ru.obabok.oui.widget.text.OTextBoxWidget;

import java.util.*;

public class BotManagerScreen extends OBaseScreen {
    OTextBoxWidget newBotWidget;
    public BotManagerScreen() {
        super("Bots manager");
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new OStringWidget(width / 2 - (font.width(title) / 2), 10, font.width(title), font.lineHeight, title));
        BotManagerListWidget botsList = new BotManagerListWidget(minecraft, 200, height - 70, 55, 40);
        botsList.setX(width / 2 - 100);

        Collection<PlayerInfo> players = getBots();
        for (PlayerInfo player : players) {
            botsList.addEntry(new BotManagerListElement(player.getProfile().name()));
        }
        addRenderableWidget(botsList);

        newBotWidget = new OTextBoxWidget(width / 2 - 85, 25, 100, 20, Component.empty());
        newBotWidget.setOnEnter(() -> {
            spawnBot(newBotWidget.getText());
        });
        addRenderableWidget(OButtonWidget.builder("Spawn")
                .pos(width / 2 + 30, 25)
                .size(50, 20)
                .onMouseClick(button -> {
                    spawnBot(newBotWidget.getText());
                }).build());

        addRenderableWidget(newBotWidget);
    }

    private Collection<PlayerInfo> getBots(){
        if(minecraft.getConnection() != null){
            List<PlayerInfo> playerInfos = new ArrayList<>(minecraft.getConnection().getListedOnlinePlayers());
            playerInfos.removeIf(playerInfo -> {
               if(playerInfo.getTeam() != null){
                   return !playerInfo.getTeam().getName().equals(BotManagerModuleInit.config.BotManager.botTeam);
               }
               return true;
            });
            return playerInfos;
        }
        return List.of();
    }

    private void killBot(String name){
        if(minecraft.getConnection() != null){
            minecraft.getConnection().sendCommand("player %s kill".formatted(name));
            minecraft.gui.setScreen(new BotManagerScreen());
        }
    }

    private void spawnBot(String name){
        if(minecraft.getConnection() != null){
            minecraft.getConnection().sendCommand("player %s spawn".formatted(name));
            minecraft.gui.setScreen(new BotManagerScreen());
        }
    }

    private class BotManagerListWidget extends ObjectSelectionList<BotManagerListElement> {
        public BotManagerListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return this.width - 30;
        }


        @Override
        protected int scrollBarX() {
            return this.getX() + this.width - 10;
        }

        public int addEntry(BotManagerListElement entry) {
            super.addEntry(entry);
            return 0;
        }
    }

    private class BotManagerListElement extends ObjectSelectionList.Entry<BotManagerListElement> {
        public String text;
        private final OButtonWidget killButton = OButtonWidget.builder("X")
                .pos(0,0)
                .size(20, 20)
                .onMouseClick(button -> {
                    killBot(text);
                })
                .build();

        public BotManagerListElement(String text) {
            this.text = text;
            addWidget(killButton);
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.literal("a");
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            //graphics.outline(getX(), getY(), getContentWidth(), getContentHeight(), CommonColors.WHITE);
            graphics.text(font, this.text, getX() + getContentWidth() / 2 - font.width(text) / 2, getY() + getContentHeight() / 2 - font.lineHeight / 2, CommonColors.WHITE);

            killButton.setPosition(getX() + 10, getY() + 10);
            killButton.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {

            return super.mouseClicked(event, doubleClick);
        }
    }
}

