package ru.obabok.oui.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigFile {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static JsonObject raw;
    private static Path path;

    private ConfigFile() {}

    public static synchronized Path path() {
        if (path == null) {
            path = FabricLoader.getInstance().getConfigDir().resolve(Product.ID).resolve(Product.ID + ".json");
        }
        return path;
    }

    public static synchronized JsonObject raw() {
        if (raw == null) {
            raw = read(path());
            if (raw.isEmpty()) {
                write();
            }
        }
        return raw;
    }

    public static synchronized void write() {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            Path tmp = p.resolveSibling(p.getFileName() + ".tmp");
            Files.writeString(tmp, GSON.toJson(raw()));
            Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Product.LOGGER.error("Не удалось сохранить конфиг", e);
        }
    }

    public static JsonObject read(Path p) {
        if (!Files.exists(p)) return new JsonObject();
        try (JsonReader r = new JsonReader(Files.newBufferedReader(p))) {
            r.setStrictness(Strictness.LENIENT);
            JsonElement el = JsonParser.parseReader(r);
            return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (Exception e) {
            Product.LOGGER.error("Не удалось прочитать конфиг {}", p, e);
            return new JsonObject();
        }
    }
}
