package ru.obabok.oui.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class SectionConfigManager<T extends OConfigInstance> extends OConfigManager<T> {
    private final Class<T> type;
    private final String section;

    SectionConfigManager(Class<T> type, String section, String keyBase) {
        this.type = type;
        this.section = section;
        this.keyBase = keyBase;
        load();
    }

    @Override
    public void load() {
        try {
            instance = type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("У конфиг-класса нужен конструктор без аргументов: " + type, e);
        }
        JsonElement el = ConfigFile.raw().get(section);
        if (el != null && el.isJsonObject()) {
            try {
                T loaded = ConfigFile.GSON.fromJson(el, type);
                if (loaded != null) instance = loaded;
            } catch (JsonParseException e) {
                Product.LOGGER.error("Секция '{}' конфига битая, использую дефолт", section, e);
            }
        }
    }

    @Override
    public void save() {
        ConfigFile.raw().add(section, ConfigFile.GSON.toJsonTree(instance));
    }
}
