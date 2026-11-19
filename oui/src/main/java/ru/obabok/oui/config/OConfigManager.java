package ru.obabok.oui.config;

public abstract class OConfigManager<T extends OConfigInstance> {
    protected T instance = null;
    protected String keyBase = "";

    public T getInstance() { return instance; }
    public String getKeyBase() { return keyBase; }
    public abstract void load();
    public abstract void save();
}

