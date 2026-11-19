package ru.obabok.favoriteitems;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class FavoriteItemsConfig implements OConfigInstance {

    @Module
    public FavoriteItemsModule FavoriteItems = new FavoriteItemsModule();

    public static class FavoriteItemsModule {
        @ConfigValue
        public boolean enabled = false;
    }
}
