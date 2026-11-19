package ru.obabok.oui.util;

import java.util.Locale;

public final class ConfigKeys {
    public static String snakeCase(String s) {
        return s.replaceAll("([a-z0-9])([A-Z]+)", "$1_$2").toLowerCase(Locale.ROOT);
    }
}
