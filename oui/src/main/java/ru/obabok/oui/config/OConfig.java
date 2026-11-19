package ru.obabok.oui.config;

import java.util.*;

public final class OConfig {
    private static final Map<String, OConfigManager<?>> BY_SECTION = new LinkedHashMap<>();
    private static final Map<Class<?>, OConfigManager<?>> BY_TYPE = new HashMap<>();
    private static final List<Runnable> SAVE_LISTENERS = new ArrayList<>();

    private OConfig() {}

    public static <T extends OConfigInstance> T register(String section, Class<T> type, String keyBase) {
        if (BY_SECTION.containsKey(section)) {
            throw new IllegalStateException("Секция уже зарегистрирована: " + section);
        }
        SectionConfigManager<T> mgr = new SectionConfigManager<>(type, section, keyBase);
        BY_SECTION.put(section, mgr);
        BY_TYPE.put(type, mgr);
        return mgr.getInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T extends OConfigInstance> T get(Class<T> type) {
        OConfigManager<?> mgr = BY_TYPE.get(type);
        if (mgr == null) {
            throw new IllegalStateException("Конфиг не зарегистрирован: " + type.getSimpleName()
                    + ". Забыл OConfig.register() в entrypoint модуля?");
        }
        return (T) mgr.getInstance();
    }

    public static Collection<OConfigManager<?>> managers() {
        return BY_SECTION.values();
    }

    public static void onSave(Runnable listener) { SAVE_LISTENERS.add(listener); }

    public static void saveAll() {
        BY_SECTION.values().forEach(OConfigManager::save);
        ConfigFile.write();
        SAVE_LISTENERS.forEach(Runnable::run);
    }
}