package ru.obabok.spectator;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SpectatorScreen extends Screen {
    private SpectatorListWidget playerList;
    private SpectatorListWidget entityList;
    private static final Comparator<PlayerInfo> ORDERING = Comparator.comparing(a -> a.getProfile().name());
    private static final Comparator<Entity> entityComparator = Comparator.comparing(a -> a.getName().getString());

    public SpectatorScreen() {
        super(Component.literal("SpectatorScreen"));
    }
    @Override
    protected void init() {
        super.init();

        playerList = new SpectatorListWidget(minecraft, width/3, height - 30, 10, 40);
        entityList = new SpectatorListWidget(minecraft, width/3, height - 30, 10, 40);

        if(Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().player != null){
            List<SpectatorScreenProfileElement> players = Minecraft.getInstance().getConnection().getListedOnlinePlayers().stream()
                    .sorted(ORDERING)
                    .map(PlayerInfo::getProfile)
                    .filter(profile -> profile != Minecraft.getInstance().player.getGameProfile())
                    .map(SpectatorScreenProfileElement::new).toList();
            for (int i = 0; i < players.size(); i++) {
                playerList.addEntry(players.get(i));
            }
            List<Entity> tempEntityList = new ArrayList<>();

            Minecraft.getInstance().level.entitiesForRendering().forEach(tempEntityList::add);
            tempEntityList.sort(entityComparator);
            for (int i = 0; i < tempEntityList.size(); i++) {
                entityList.addEntry(new SpectatorScreenEntityElement(tempEntityList.get(i)));
            }

            playerList.setX(50);
            entityList.setX(width - (entityList.getWidth() + 50));
            addWidget(playerList);
            addWidget(entityList);
            playerList.setScrollAmount(0);
            entityList.setScrollAmount(0);
        }

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        playerList.extractRenderState(graphics, mouseX, mouseY, a);
        entityList.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private class SpectatorListWidget extends ObjectSelectionList<SpectatorScreenElement> {
        public SpectatorListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return this.width - 30;
        }

//        @Override
//        protected void drawMenuListBackground(DrawContext context) {
//            //super.drawMenuListBackground(context);
//            context.fill(getX(), getY(), getRight(), getBottom(), grayColor);
//        }
//


        @Override
        protected int scrollBarX() {
            return this.getX() + this.width - 10;
        }

        public int addEntry(SpectatorScreenElement entry) {
            super.addEntry(entry);
            return 0;
        }
    }



    private class SpectatorScreenElement extends ObjectSelectionList.Entry<SpectatorScreenElement> {
        public String text;

        public SpectatorScreenElement(String text) {
            this.text = text;
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.literal("a");
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            //graphics.outline(getX(), getY(), getContentWidth(), getContentHeight(), CommonColors.WHITE);
            graphics.text(font, this.text, getX() + getContentWidth() / 2 - font.width(text) / 2, getY() + getContentHeight() / 2 - font.lineHeight / 2, CommonColors.WHITE);

        }
    }

    private class SpectatorScreenEntityElement extends SpectatorScreenElement {
        public final Entity entity;
        public final UUID entityUUID;
        private List<ClientTooltipComponent> tooltip;
        public SpectatorScreenEntityElement(Entity entity){
            super(Component.translatable(entity.getName().getString()).getString());
            this.entity = entity;
            this.entityUUID = entity.getUUID();
            init();
        }
        public SpectatorScreenEntityElement(String text, UUID uuid){
            super(text);
            this.entity = null;
            this.entityUUID = uuid;
            init();
        }

        private void init(){
            tooltip = new ArrayList<>();
            tooltip.add(new ClientTextTooltip(Component.literal("").getVisualOrderText()));
            tooltip.add(new ClientTextTooltip(Component.literal("").getVisualOrderText()));
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if(Minecraft.getInstance().getConnection() != null){
                Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(entityUUID));
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            super.extractContent(graphics, mouseX, mouseY, hovered, a);

            if(entity instanceof LivingEntity livingEntity){
                InventoryScreen.extractEntityInInventoryFollowsMouse(
                        graphics,
                        getX(),
                        getY(),
                        getX() + 50,
                        getY() + getContentHeight() ,
                        17,
                        0,
                        mouseX,
                        mouseY,
                        livingEntity
                );

            } else if (entity instanceof ItemEntity itemEntity){
                graphics.item(itemEntity.getItem(), getX() + 15, getY() + 10);
            }

            if(hovered){

                tooltip.set(0, new ClientTextTooltip(Component.literal(entityUUID.toString()).getVisualOrderText()));
                if(entity != null){
                    tooltip.set(1, new ClientTextTooltip(Component.literal(entity.position().toString()).getVisualOrderText()));
                }
                graphics.pose().pushMatrix();
                graphics.pose().translate(getX() + 50, getY() + 20);
                graphics.pose().scale(0.8f);
                graphics.tooltip(font, tooltip, 0, 0, DefaultTooltipPositioner.INSTANCE,
                        null, false);
                graphics.pose().popMatrix();
            }
        }
    }

    private class SpectatorScreenProfileElement extends SpectatorScreenEntityElement {

        public final GameProfile profile;
        private final CompletableFuture<Optional<PlayerSkin>> skinTexturesSupplier;

        public SpectatorScreenProfileElement(GameProfile profile){
            super(profile.name(), profile.id());
            this.profile = profile;
            this.skinTexturesSupplier = Minecraft.getInstance().getSkinManager().get(profile);
            this.text = profile.name();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            PlayerSkin skin = skinTexturesSupplier.getNow(Optional.empty()).orElseGet(DefaultPlayerSkin::getDefaultSkin);
            PlayerFaceExtractor.extractRenderState(graphics, skin, getX() + getContentWidth() / 2 - font.width(text) / 2 - 30, getY() + getContentHeight() / 2 - 10, 20, CommonColors.WHITE);
            super.extractContent(graphics, mouseX, mouseY, hovered, a);
        }

    }

}
