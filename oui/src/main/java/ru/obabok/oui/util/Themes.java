package ru.obabok.oui.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.obabok.oui.model.OColor;
import ru.obabok.oui.model.Theme;

import java.io.*;

public class Themes {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(int.class, new ColorTypeAdapter())
            .create();

    private static Theme currentTheme;

    static {
        currentTheme = new Theme(
                new Theme.Text(OColor.getColor("FFFFFFFF"), OColor.getColor("FF000099")),
                new Theme.Decorations(OColor.getColor("FF444444"), OColor.getColor("FFFFFFFF"), OColor.getColor("22000000"))
        );
    }

    public static Theme getCurrentTheme(){
        return currentTheme;
    }

    public static void saveThemeToFile(Theme theme, File file) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(theme, writer);
        }
    }

    public static void loadThemeFromFile(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            currentTheme = gson.fromJson(reader, Theme.class);
        }
    }


    public static String argbIntToString(int argbColor) {
        int a = (argbColor >>> 24) & 0xFF;
        int r = (argbColor >>> 16) & 0xFF;
        int g = (argbColor >>> 8)  & 0xFF;
        int b = argbColor          & 0xFF;

        return String.format("%02X%02X%02X%02X", a, r, g, b);
    }

    private static class ColorTypeAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            String hex = String.format("%08X", value);
            out.value(hex);
        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            String hex = in.nextString();
            return (int) Long.parseLong(hex, 16);
        }
    }
}
