package com.amaki.module.setting;

/**
 * Boolean настройка (вкл/выкл)
 */
public class BooleanSetting extends Setting<Boolean> {
    
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        this.value = !this.value;
    }

    public boolean isEnabled() {
        return value;
    }
}
