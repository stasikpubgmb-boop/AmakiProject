package com.amaki;

import com.amaki.gui.ClickGuiScreen;
import com.amaki.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * AmakiProject - Премиум утилитарный мод с профессиональным ClickGUI
 * 
 * Основные функции:
 * - ClickGUI в стиле Nursultan/Celestial/Expensive (темный, современный)
 * - Blur эффект фона
 * - Кастомный TTF шрифт (Inter)
 * - Модульная система с настройками
 * - Плавные анимации
 * 
 * Версия: 1.0.0
 * Minecraft: 1.21.4
 * Fabric API: 0.112.0+
 */
public class AmakiProject implements ClientModInitializer {
    
    public static final String MOD_ID = "amakiproject";
    public static final String MOD_NAME = "AmakiProject";
    public static final String VERSION = "1.0.0";
    
    private static AmakiProject instance;
    private ModuleManager moduleManager;
    
    // Keybind для открытия GUI (Right Shift)
    private KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        
        System.out.println("=================================");
        System.out.println(MOD_NAME + " v" + VERSION);
        System.out.println("Premium ClickGUI initialized");
        System.out.println("=================================");
        
        // Инициализация ModuleManager
        moduleManager = ModuleManager.getInstance();
        
        // Регистрация кейбинда для открытия GUI
        registerKeybindings();
        
        // Регистрация обработчика тиков
        registerTickHandler();
    }

    /**
     * Регистрация кейбиндов
     */
    private void registerKeybindings() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.amakiproject.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT, // Right Shift для открытия GUI
            "category.amakiproject.main"
        ));
    }

    /**
     * Регистрация обработчика клиентских тиков
     */
    private void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Проверяем нажатие кейбинда для открытия GUI
            while (openGuiKey.wasPressed()) {
                openClickGui();
            }
        });
    }

    /**
     * Открыть ClickGUI
     */
    public void openClickGui() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null) {
            mc.setScreen(new ClickGuiScreen());
        }
    }

    /**
     * Получить singleton instance мода
     */
    public static AmakiProject getInstance() {
        return instance;
    }

    /**
     * Получить ModuleManager
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
