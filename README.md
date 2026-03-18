# AmakiProject

**Premium utility mod для Minecraft 1.21.4 Fabric с профессиональным ClickGUI**

## 🎯 Особенности

### ClickGUI в стиле Nursultan/Celestial/Expensive
- **Темный современный дизайн** без неона, rainbow эффектов или читерского вида
- **Gaussian Blur фон** - размытие игры за пределами GUI окна
- **Сплошной темный фон** окна GUI (#0D0D0D, alpha ~93-95%)
- **Кастомный TTF шрифт** (Inter Regular) для профессионального вида
- **Плавные анимации** - fade in/out, раскрытие настроек модулей
- **Верхняя панель категорий** с иконками и индикаторами

### Интерфейс

#### Верхняя панель
- Квадратная белая иконка [A] слева
- Горизонтальные вкладки категорий:
  - **Combat** (⚔) - активная: белый текст, белый ✗
  - **Player** (◉)
  - **Movement** (➜)
  - **Visuals** (○)
  - **Misc** (×)
- Закругление углов вкладок ~5px
- Высота панели 34px

#### Список модулей
- Вертикальный список с тонким разделителем #333333
- Название модуля слева, статус справа (✓/○)
- **ЛКМ** - включить/выключить модуль
- **ПКМ** - раскрыть настройки (с анимацией)
- Активный модуль: темно-зеленый фон #1A2A1A

#### Настройки модулей (с отступом 10px)
1. **Boolean** - квадрат 12×12, заполнен белым если включен
2. **Slider (double)** - тонкая линия с заполнением, значение справа
3. **Keybind** - текст "Keybind" + название клавиши (серый если не назначен)

### Цветовая схема
```
Фон окна:         #0D0D0D (alpha ~93%)
Фон модуля:       #111111 - #151515
Hover эффект:     #1E1E1E
Активный модуль:  #1A2A1A (темно-зеленый)
Текст:            #E0E0E0
Слайдер:          #AAAAAA
Разделитель:      #333333
```

## 🎮 Управление

- **Right Shift (или Insert)** - открыть ClickGUI
- **Escape** - закрыть GUI
- **Скролл мыши** - прокрутка списка модулей
- **ЛКМ на модуле** - вкл/выкл модуль
- **ПКМ на модуле** - раскрыть настройки
- **Клик на Boolean** - переключить
- **Drag на Slider** - изменить значение
- **Клик на Keybind** - начать прослушивание клавиши
  - Escape - отменить
  - Delete/Backspace - убрать бинд

## 🛠️ Технические детали

### Структура проекта
```
AmakiProject/
├── src/main/java/com/amaki/
│   ├── AmakiProject.java              # Main mod initializer
│   ├── gui/
│   │   ├── ClickGuiScreen.java        # Главный экран GUI
│   │   ├── Category.java              # Enum категорий
│   │   ├── ModuleButton.java          # Кнопка модуля с настройками
│   │   ├── components/                # Компоненты настроек
│   │   │   ├── SettingComponent.java
│   │   │   ├── BooleanComponent.java
│   │   │   ├── SliderComponent.java
│   │   │   └── BindComponent.java
│   │   └── render/
│   │       ├── CustomFont.java        # TTF font wrapper
│   │       └── BlurRenderer.java      # Blur эффект фона
│   └── module/
│       ├── Module.java                # Базовый класс модуля
│       ├── ModuleManager.java         # Singleton менеджер
│       └── setting/                   # Типы настроек
│           ├── Setting.java
│           ├── BooleanSetting.java
│           ├── DoubleSetting.java
│           └── KeybindSetting.java
└── src/main/resources/
    └── assets/amakiproject/
        ├── fonts/
        │   └── inter.ttf              # Inter Regular TTF font
        └── font/
            └── custom.json            # Font provider config
```

### Реализация Blur эффекта

Blur реализован в `BlurRenderer.java`:
```java
// 1. Применяем темный полупрозрачный оверлей к фону игры
// 2. Эмулируем Gaussian blur эффект (радиус ~6-8)
// 3. Само окно GUI рисуется поверх с сплошным темным фоном
```

Для производительности используется простой оверлей. В продакшене можно добавить shader-based blur.

### Кастомный шрифт

TTF шрифт загружается через font providers в `assets/amakiproject/font/custom.json`:
```json
{
  "providers": [
    {
      "type": "ttf",
      "file": "amakiproject:fonts/inter.ttf",
      "shift": [0.0, 0.0],
      "size": 11.0,
      "oversample": 2.0
    }
  ]
}
```

`CustomFont.java` оборачивает `TextRenderer` для удобного использования.

### Анимации

1. **Fade in/out** - плавное появление GUI (alpha 0→1 за ~200ms)
2. **Expand animation** - раскрытие настроек модуля (progress 0→1)
3. **Smooth scrolling** - плавная прокрутка списка модулей

## 📦 Сборка

```bash
./gradlew build
```

Готовый JAR будет в `build/libs/`

## 🔧 Разработка

```bash
# Запуск клиента для тестирования
./gradlew runClient

# Генерация sources
./gradlew genSources
```

## 📋 Модули (заглушки для демонстрации GUI)

Все модули - **чисто визуальные заглушки** для демонстрации ClickGUI. Никаких реальных чит-функций.

### Combat
- **KillAura** - Players, Mobs, Range (3.0-6.0), CPS (1-20), Keybind
- **Velocity** - Horizontal, Vertical
- **Criticals** - OnlyAura

### Player
- **FastUse** - Food, Potions
- **NoFall** - Distance (2.0-10.0)

### Movement
- **Sprint** - OmniSprint
- **Speed** - Speed (1.0-3.0), OnGround
- **Fly** - Speed, Glide

### Visuals
- **ESP** - Players, Mobs, Items, Width
- **Fullbright**
- **Nametags** - Health, Distance, Scale
- **HUD** - Watermark, ArrayList (включен по умолчанию)

### Misc
- **AutoGG** - OnKill, OnDeath
- **MiddleClickFriend**

## 📝 Лицензия

MIT License

## 🤝 Авторы

Amaki Team

---

**Версия:** 1.0.0  
**Minecraft:** 1.21.4  
**Fabric Loader:** 0.16.9  
**Fabric API:** 0.112.0+1.21.4
