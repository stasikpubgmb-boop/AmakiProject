package com.amaki.module.setting;

/**
 * Абстрактный класс для всех настроек модуля
 */
public abstract class Setting<T> {
    protected final String name;
    protected T value;
    protected boolean visible = true;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
