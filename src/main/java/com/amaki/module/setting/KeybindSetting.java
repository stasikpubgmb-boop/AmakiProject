package com.amaki.module.setting;

import org.lwjgl.glfw.GLFW;

/**
 * Keybind настройка (код клавиши GLFW)
 */
public class KeybindSetting extends Setting<Integer> {
    
    public KeybindSetting(String name, int defaultKey) {
        super(name, defaultKey);
    }

    public int getKey() {
        return value;
    }

    public void setKey(int key) {
        this.value = key;
    }

    /**
     * Возвращает название клавиши для отображения
     */
    public String getKeyName() {
        if (value == GLFW.GLFW_KEY_UNKNOWN || value == -1) {
            return "None";
        }
        String name = GLFW.glfwGetKeyName(value, 0);
        if (name != null) {
            return name.toUpperCase();
        }
        // Для специальных клавиш
        return switch (value) {
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            default -> "KEY_" + value;
        };
    }
}
