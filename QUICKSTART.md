# AmakiProject - QuickStart

## ⚡ Быстрый старт

### 1️⃣ Требования
- Java 21+
- Gradle 8.11.1+
- Minecraft 1.21.4
- Fabric Loader 0.16.9+
- Fabric API 0.112.0+

### 2️⃣ Установка зависимостей

```bash
# Если у вас нет Gradle wrapper
gradle wrapper --gradle-version 8.11.1

# Генерация source mappings
./gradlew genSources
```

### 3️⃣ Запуск для тестирования

```bash
# Запуск клиента
./gradlew runClient
```

### 4️⃣ Сборка мода

```bash
# Сборка JAR
./gradlew build

# Готовый файл в:
# build/libs/amakiproject-1.0.0.jar
```

## 🎮 Использование

### Открытие ClickGUI
Нажмите **Right Shift** (или **Insert**) в игре

### Управление в GUI

#### Навигация
- **Клик по вкладке** - переключить категорию
- **Скролл мыши** - прокрутка списка модулей

#### Модули
- **ЛКМ на модуле** - включить/выключить
- **ПКМ на модуле** - раскрыть настройки

#### Настройки
- **Boolean**: клик для toggle
- **Slider**: drag для изменения значения
- **Keybind**: клик, затем нажать клавишу
  - `Escape` - отменить
  - `Delete/Backspace` - убрать бинд

## 🛠️ Добавление нового модуля

### Шаг 1: Создать класс модуля

```java
package com.amaki.module.impl;

import com.amaki.gui.Category;
import com.amaki.module.Module;
import com.amaki.module.setting.*;

public class MyModule extends Module {
    
    public MyModule() {
        super("MyModule", "Описание модуля", Category.MISC);
        
        // Добавляем настройки
        addSetting(new BooleanSetting("Enabled", true));
        addSetting(new DoubleSetting("Speed", 1.0, 0.5, 2.0, 0.1));
        addSetting(new KeybindSetting("Keybind", -1));
    }
    
    @Override
    protected void onEnable() {
        // Логика при включении
        System.out.println("MyModule enabled!");
    }
    
    @Override
    protected void onDisable() {
        // Логика при выключении
        System.out.println("MyModule disabled!");
    }
}
```

### Шаг 2: Зарегистрировать в ModuleManager

Добавьте в `ModuleManager.initModules()`:

```java
private void initModules() {
    // ... существующие модули ...
    
    modules.add(new MyModule());
}
```

Готово! Модуль появится в GUI автоматически.

## 🎨 Кастомизация GUI

### Изменение цветов

Отредактируйте константы в `ClickGuiScreen.java`:

```java
private static final int COLOR_WINDOW_BG = 0xEE0D0D0D;      // Фон окна
private static final int COLOR_TAB_ACTIVE = 0x1FFFFFFF;     // Активная вкладка
private static final int COLOR_BG_ENABLED = 0xFF1A2A1A;     // Включенный модуль
```

### Изменение размера GUI

```java
private static final int GUI_WIDTH = 460;   // Ширина
private static final int GUI_HEIGHT = 380;  // Высота
```

### Изменение шрифта

1. Замените `assets/amakiproject/fonts/inter.ttf` на свой TTF файл
2. Отредактируйте `assets/amakiproject/font/custom.json`:

```json
{
  "providers": [
    {
      "type": "ttf",
      "file": "amakiproject:fonts/your_font.ttf",
      "size": 11.0,  // Размер шрифта
      "oversample": 2.0
    }
  ]
}
```

## 📦 Добавление нового типа настройки

### Пример: ColorSetting

#### 1. Создать класс Setting

```java
package com.amaki.module.setting;

public class ColorSetting extends Setting<Integer> {
    
    public ColorSetting(String name, int defaultColor) {
        super(name, defaultColor);
    }
    
    public int getRed() {
        return (value >> 16) & 0xFF;
    }
    
    public int getGreen() {
        return (value >> 8) & 0xFF;
    }
    
    public int getBlue() {
        return value & 0xFF;
    }
    
    public void setRGB(int r, int g, int b) {
        value = (r << 16) | (g << 8) | b;
    }
}
```

#### 2. Создать Component

```java
package com.amaki.gui.components;

public class ColorComponent extends SettingComponent<ColorSetting> {
    
    public ColorComponent(ColorSetting setting) {
        super(setting);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Рендер color picker
        int color = setting.getValue();
        context.fill((int)x, (int)y, (int)(x+20), (int)(y+20), 0xFF000000 | color);
    }
    
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        // Открыть color picker
    }
}
```

#### 3. Зарегистрировать в ModuleButton

В `ModuleButton.java` конструкторе:

```java
for (Setting<?> setting : module.getSettings()) {
    if (setting instanceof BooleanSetting booleanSetting) {
        settingComponents.add(new BooleanComponent(booleanSetting));
    } else if (setting instanceof DoubleSetting doubleSetting) {
        settingComponents.add(new SliderComponent(doubleSetting));
    } else if (setting instanceof KeybindSetting keybindSetting) {
        settingComponents.add(new BindComponent(keybindSetting));
    } else if (setting instanceof ColorSetting colorSetting) {
        settingComponents.add(new ColorComponent(colorSetting));
    }
}
```

## 🐛 Отладка

### Включение логирования

```java
// В AmakiProject.java
@Override
public void onInitializeClient() {
    System.setProperty("fabric.development", "true");
    // ... остальной код ...
}
```

### Проверка загрузки модулей

```java
// В AmakiProject.onInitializeClient()
System.out.println("Loaded modules: " + moduleManager.getModules().size());
for (Module module : moduleManager.getModules()) {
    System.out.println("  - " + module.getName());
}
```

### Hot Reload

В режиме разработки используйте:

```bash
./gradlew runClient --rerun-tasks
```

## 📚 Полезные ссылки

- [Fabric Wiki](https://fabricmc.net/wiki/)
- [Minecraft Dev Wiki](https://minecraft.wiki/)
- [LWJGL Documentation](https://www.lwjgl.org/)

## ❓ FAQ

### Q: GUI не открывается при нажатии Right Shift
**A:** Проверьте, что Fabric API установлен. Проверьте логи на ошибки.

### Q: Шрифт выглядит некрасиво
**A:** Убедитесь, что `inter.ttf` загружен корректно. Попробуйте изменить `size` в `custom.json`.

### Q: Blur не работает
**A:** Текущая версия использует простое затемнение. Для настоящего blur см. `BLUR_GUIDE.md`.

### Q: Как сохранить состояние модулей?
**A:** Реализуйте ConfigManager (см. `ARCHITECTURE.md`, раздел "Возможные улучшения").

### Q: Можно ли использовать в multiplayer?
**A:** Да, но некоторые модули могут быть запрещены античитом сервера. Текущие модули - заглушки без реальной функциональности.

---

**Успешной разработки! 🚀**

Если возникли вопросы, читайте подробную документацию:
- `README.md` - общее описание
- `ARCHITECTURE.md` - архитектура проекта
- `BLUR_GUIDE.md` - продвинутая реализация blur
