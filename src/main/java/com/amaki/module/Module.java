package com.amaki.module;

import com.amaki.gui.Category;
import com.amaki.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для всех модулей
 */
public class Module {
    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled = false;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    /**
     * Добавить настройку к модулю
     */
    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    /**
     * Включить/выключить модуль
     */
    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Вызывается при включении модуля
     */
    protected void onEnable() {
        // Override в подклассах
    }

    /**
     * Вызывается при выключении модуля
     */
    protected void onDisable() {
        // Override в подклассах
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
