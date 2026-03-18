package com.amaki.gui.components;

import com.amaki.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;

/**
 * Абстрактный компонент для рендеринга настройки модуля
 */
public abstract class SettingComponent<T extends Setting<?>> {
    protected final T setting;
    protected float x, y, width, height;
    protected boolean hovered = false;

    public SettingComponent(T setting) {
        this.setting = setting;
        this.height = 18; // Стандартная высота компонента
    }

    /**
     * Установить позицию и размер компонента
     */
    public void setPosition(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    /**
     * Отрисовка компонента
     */
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    /**
     * Клик мыши
     */
    public abstract void mouseClicked(double mouseX, double mouseY, int button);

    /**
     * Отпускание мыши
     */
    public void mouseReleased(double mouseX, double mouseY, int button) {
        // Override если нужно
    }

    /**
     * Перемещение мыши (для слайдеров)
     */
    public void mouseMoved(double mouseX, double mouseY) {
        // Override если нужно
    }

    /**
     * Проверка наведения курсора
     */
    protected boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }

    public float getHeight() {
        return height;
    }

    public T getSetting() {
        return setting;
    }
}
