package com.amaki.module;

import com.amaki.gui.Category;
import com.amaki.module.setting.BooleanSetting;
import com.amaki.module.setting.DoubleSetting;
import com.amaki.module.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton менеджер всех модулей
 */
public class ModuleManager {
    private static ModuleManager instance;
    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        initModules();
    }

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    /**
     * Инициализация модулей-заглушек для демонстрации GUI
     */
    private void initModules() {
        // Combat модули
        Module killaura = new Module("KillAura", "Автоматическая атака", Category.COMBAT);
        killaura.addSetting(new BooleanSetting("Players", true));
        killaura.addSetting(new BooleanSetting("Mobs", false));
        killaura.addSetting(new DoubleSetting("Range", 3.5, 3.0, 6.0, 0.1));
        killaura.addSetting(new DoubleSetting("CPS", 12.0, 1.0, 20.0, 1.0));
        killaura.addSetting(new KeybindSetting("Bind", GLFW.GLFW_KEY_R));
        modules.add(killaura);

        Module velocity = new Module("Velocity", "Уменьшение отброса", Category.COMBAT);
        velocity.addSetting(new DoubleSetting("Horizontal", 0.0, 0.0, 100.0, 1.0));
        velocity.addSetting(new DoubleSetting("Vertical", 0.0, 0.0, 100.0, 1.0));
        modules.add(velocity);

        Module criticals = new Module("Criticals", "Автоматические криты", Category.COMBAT);
        criticals.addSetting(new BooleanSetting("OnlyAura", false));
        modules.add(criticals);

        // Player модули
        Module fastUse = new Module("FastUse", "Быстрое использование", Category.PLAYER);
        fastUse.addSetting(new BooleanSetting("Food", true));
        fastUse.addSetting(new BooleanSetting("Potions", true));
        modules.add(fastUse);

        Module noFall = new Module("NoFall", "Убрать урон от падения", Category.PLAYER);
        noFall.addSetting(new DoubleSetting("Distance", 3.0, 2.0, 10.0, 0.5));
        modules.add(noFall);

        // Movement модули
        Module sprint = new Module("Sprint", "Автоматический бег", Category.MOVEMENT);
        sprint.addSetting(new BooleanSetting("OmniSprint", false));
        modules.add(sprint);

        Module speed = new Module("Speed", "Увеличение скорости", Category.MOVEMENT);
        speed.addSetting(new DoubleSetting("Speed", 1.5, 1.0, 3.0, 0.1));
        speed.addSetting(new BooleanSetting("OnGround", true));
        modules.add(speed);

        Module fly = new Module("Fly", "Полёт", Category.MOVEMENT);
        fly.addSetting(new DoubleSetting("Speed", 1.0, 0.1, 5.0, 0.1));
        fly.addSetting(new BooleanSetting("Glide", false));
        modules.add(fly);

        // Visuals модули
        Module esp = new Module("ESP", "Подсветка сущностей", Category.VISUALS);
        esp.addSetting(new BooleanSetting("Players", true));
        esp.addSetting(new BooleanSetting("Mobs", true));
        esp.addSetting(new BooleanSetting("Items", false));
        esp.addSetting(new DoubleSetting("Width", 2.0, 1.0, 5.0, 0.5));
        modules.add(esp);

        Module fullbright = new Module("Fullbright", "Полная яркость", Category.VISUALS);
        modules.add(fullbright);

        Module nametags = new Module("Nametags", "Улучшенные таблички", Category.VISUALS);
        nametags.addSetting(new BooleanSetting("Health", true));
        nametags.addSetting(new BooleanSetting("Distance", false));
        nametags.addSetting(new DoubleSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        modules.add(nametags);

        Module hud = new Module("HUD", "Отображение интерфейса", Category.VISUALS);
        hud.addSetting(new BooleanSetting("Watermark", true));
        hud.addSetting(new BooleanSetting("ArrayList", true));
        hud.setEnabled(true); // Включен по умолчанию
        modules.add(hud);

        // Misc модули
        Module autoGG = new Module("AutoGG", "Авто сообщения", Category.MISC);
        autoGG.addSetting(new BooleanSetting("OnKill", true));
        autoGG.addSetting(new BooleanSetting("OnDeath", false));
        modules.add(autoGG);

        Module middleClick = new Module("MiddleClickFriend", "ПКМ - друзья", Category.MISC);
        modules.add(middleClick);
    }

    /**
     * Получить все модули
     */
    public List<Module> getModules() {
        return modules;
    }

    /**
     * Получить модули по категории
     */
    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Получить модуль по имени
     */
    public Module getModule(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
