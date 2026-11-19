package ru.obabok.oui.util;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class TranslationHelper {

    public static boolean canTranslate(String key) {
        return Language.getInstance().has(key);
    }

    public static Component translateOrGet(String key) {
        if (canTranslate(key)) {
            return Component.translatable(key);
        }
        return Component.literal(key);
    }
}
