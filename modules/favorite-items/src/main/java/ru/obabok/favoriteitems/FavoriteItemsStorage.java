package ru.obabok.favoriteitems;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import ru.obabok.o_client.References;
import ru.obabok.oui.config.Product;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteItemsStorage {

    private static final Set<String> favoriteItems = new HashSet<>();
    private static Path saveFile;
    private static final Set<String> IGNORED = Set.of("minecraft:damage", "minecraft:repair_cost");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init(Minecraft minecraft){
        String gameName = getGameName(minecraft);
        saveFile = FabricLoader.getInstance().getConfigDir().resolve(Product.ID).resolve("FavoriteItems").resolve(gameName);
        try {
            Files.createDirectories(saveFile.getParent());
        } catch (IOException e) {
            References.LOGGER.error(e.getMessage());
        }
        load();
    }

    public static void addFavoriteItem(ItemStack item) {
        favoriteItems.add(getItemStackKey(item));
        saveFavorites();
    }

    public static void removeFavoriteItem(ItemStack item) {
        favoriteItems.remove(getItemStackKey(item));
        saveFavorites();
    }

    public static boolean isFavoriteItem(ItemStack item) {
        return favoriteItems.contains(getItemStackKey(item));
    }

    private static void load() {
        favoriteItems.clear();
        if (saveFile == null || !Files.exists(saveFile)) return;

        try (Reader reader = Files.newBufferedReader(saveFile)) {
            JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement el : arr) {
                favoriteItems.add(el.getAsString());
            }
        } catch (IOException e) {
            References.LOGGER.error(e.getMessage());
        }
    }

    private static void saveFavorites() {
        if (saveFile == null) return;

        try (Writer writer = Files.newBufferedWriter(saveFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            JsonArray arr = new JsonArray();
            for (String s : favoriteItems) {
                arr.add(s);
            }
            GSON.toJson(arr, writer);
        } catch (IOException e) {
            References.LOGGER.error(String.valueOf(e));
        }
    }


    public static String getItemStackKey(ItemStack stack) {
        if (stack.isEmpty() || Minecraft.getInstance().level == null) return "";
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        RegistryOps<Tag> ops = access.createSerializationContext(NbtOps.INSTANCE);

        CompoundTag root = (CompoundTag) ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        root.remove("count");
        if (root.get("components") instanceof CompoundTag comps) {
            IGNORED.forEach(comps::remove);
        }
        return canonical(root);
    }

    private static String canonical(Tag tag) {
        if (tag instanceof CompoundTag c) {
            return c.keySet().stream().sorted()
                    .map(k -> k + "=" + canonical(c.get(k)))
                    .collect(Collectors.joining(",", "{", "}"));
        }
        if (tag instanceof ListTag list) {
            StringBuilder sb = new StringBuilder("[");
            for (Tag t : list) sb.append(canonical(t)).append(',');
            return sb.append(']').toString();
        }
        return tag.getId() + ":" + tag.asString();
    }

    private static String getGameName(Minecraft minecraft){
        String gameName;

        if (minecraft.isLocalServer() && minecraft.getSingleplayerServer() != null){
            gameName = "smp_" + minecraft.getSingleplayerServer().getWorldData().getLevelName() + ".json";
        } else if (minecraft.getCurrentServer() != null){
            gameName = "mp_" + minecraft.getCurrentServer().ip.replace(":", "_") + ".json";
        } else {
            gameName = "who.json";
        }

        return gameName;
    }
}
