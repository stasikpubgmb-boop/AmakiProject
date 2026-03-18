package com.amaki.gui;

import com.amaki.gui.components.*;
import com.amaki.gui.render.CustomFont;
import com.amaki.module.Module;
import com.amaki.module.setting.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Кнопка модуля в ClickGUI со списком настроек
 */
public class ModuleButton {
    private static final int MODULE_HEIGHT = 28;
    private static final int SETTINGS_OFFSET = 10;
    
    private static final int COLOR_BG = 0xFF111111;           // Фон модуля
    private static final int COLOR_BG_HOVER = 0xFF1E1E1E;     // Фон при наведении
    private static final int COLOR_BG_ENABLED = 0xFF1A2A1A;   // Темно-зеленый когда включен
    private static final int COLOR_TEXT = 0xFFE0E0E0;         // Текст
    private static final int COLOR_TOGGLE_ON = 0xFFFFFFFF;    // Белый ✓
    private static final int COLOR_TOGGLE_OFF = 0xFF666666;   // Серый когда выкл

    private final Module module;
    private final CustomFont font;
    private final List<SettingComponent<?>> settingComponents = new ArrayList<>();
    
    private float x, y, width;
    private boolean expanded = false;
    private boolean hovered = false;
    
    // Анимация раскрытия
    private float expandProgress = 0f;
    private static final float EXPAND_SPEED = 0.15f;

    public ModuleButton(Module module) {
        this.module = module;
        this.font = CustomFont.getInstance();
        
        // Создаем компоненты для каждой настройки
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting booleanSetting) {
                settingComponents.add(new BooleanComponent(booleanSetting));
            } else if (setting instanceof DoubleSetting doubleSetting) {
                settingComponents.add(new SliderComponent(doubleSetting));
            } else if (setting instanceof KeybindSetting keybindSetting) {
                settingComponents.add(new BindComponent(keybindSetting));
            }
        }
    }

    public void setPosition(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    /**
     * Рендер кнопки модуля и его настроек
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Обновляем анимацию раскрытия
        if (expanded && expandProgress < 1f) {
            expandProgress = Math.min(1f, expandProgress + EXPAND_SPEED);
        } else if (!expanded && expandProgress > 0f) {
            expandProgress = Math.max(0f, expandProgress - EXPAND_SPEED);
        }

        // Проверяем наведение на саму кнопку модуля
        hovered = mouseX >= x && mouseX <= x + width && 
                  mouseY >= y && mouseY <= y + MODULE_HEIGHT;

        // Фон кнопки модуля
        int bgColor = module.isEnabled() ? COLOR_BG_ENABLED : 
                     (hovered ? COLOR_BG_HOVER : COLOR_BG);
        
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + MODULE_HEIGHT), bgColor);

        // Название модуля слева
        font.drawString(context, module.getName(), x + 12, 
                       y + (MODULE_HEIGHT - font.getFontHeight()) / 2, COLOR_TEXT);

        // Индикатор включения справа (✓ или ○)
        String toggleIcon = module.isEnabled() ? "✓" : "○";
        int toggleColor = module.isEnabled() ? COLOR_TOGGLE_ON : COLOR_TOGGLE_OFF;
        float iconWidth = font.getStringWidth(toggleIcon);
        font.drawString(context, toggleIcon, x + width - iconWidth - 12, 
                       y + (MODULE_HEIGHT - font.getFontHeight()) / 2, toggleColor);

        // Тонкая линия-разделитель
        context.fill((int) x, (int) (y + MODULE_HEIGHT - 1), 
                    (int) (x + width), (int) (y + MODULE_HEIGHT), 0xFF333333);

        // Рендер настроек с анимацией
        if (expandProgress > 0f && !settingComponents.isEmpty()) {
            renderSettings(context, mouseX, mouseY, delta);
        }
    }

    /**
     * Рендер настроек модуля
     */
    private void renderSettings(DrawContext context, int mouseX, int mouseY, float delta) {
        float currentY = y + MODULE_HEIGHT;
        float settingsX = x + SETTINGS_OFFSET;
        float settingsWidth = width - SETTINGS_OFFSET;

        // Scissors для обрезки анимации
        int scissorHeight = (int) (getSettingsHeight() * expandProgress);
        
        // Рендерим каждую настройку
        for (SettingComponent<?> component : settingComponents) {
            if (component.getSetting().isVisible()) {
                component.setPosition(settingsX, currentY, settingsWidth);
                
                // Только если видно в scissor
                if (currentY - y - MODULE_HEIGHT < scissorHeight) {
                    component.render(context, mouseX, mouseY, delta);
                }
                
                currentY += component.getHeight();
            }
        }
    }

    /**
     * Обработка клика мыши
     */
    public void mouseClicked(double mouseX, double mouseY, int button) {
        // Клик на саму кнопку модуля
        if (mouseX >= x && mouseX <= x + width && 
            mouseY >= y && mouseY <= y + MODULE_HEIGHT) {
            
            if (button == 0) { // ЛКМ - toggle модуль
                module.toggle();
            } else if (button == 1) { // ПКМ - раскрыть настройки
                if (!settingComponents.isEmpty()) {
                    expanded = !expanded;
                }
            }
            return;
        }

        // Клик на настройки
        if (expanded && expandProgress > 0f) {
            for (SettingComponent<?> component : settingComponents) {
                if (component.getSetting().isVisible()) {
                    component.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    /**
     * Обработка отпускания мыши
     */
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded) {
            for (SettingComponent<?> component : settingComponents) {
                if (component.getSetting().isVisible()) {
                    component.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
    }

    /**
     * Обработка движения мыши (для слайдеров)
     */
    public void mouseMoved(double mouseX, double mouseY) {
        if (expanded) {
            for (SettingComponent<?> component : settingComponents) {
                if (component.getSetting().isVisible()) {
                    component.mouseMoved(mouseX, mouseY);
                }
            }
        }
    }

    /**
     * Обработка нажатия клавиши (для биндов)
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded) {
            for (SettingComponent<?> component : settingComponents) {
                if (component instanceof BindComponent bindComponent) {
                    if (bindComponent.keyPressed(keyCode, scanCode, modifiers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Получить полную высоту кнопки (включая настройки)
     */
    public float getTotalHeight() {
        float height = MODULE_HEIGHT;
        if (expandProgress > 0f) {
            height += getSettingsHeight() * expandProgress;
        }
        return height;
    }

    /**
     * Получить высоту всех настроек
     */
    private float getSettingsHeight() {
        float height = 0;
        for (SettingComponent<?> component : settingComponents) {
            if (component.getSetting().isVisible()) {
                height += component.getHeight();
            }
        }
        return height;
    }

    public Module getModule() {
        return module;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
