package com.amaki.gui.components;

import com.amaki.gui.render.CustomFont;
import com.amaki.module.setting.DoubleSetting;
import net.minecraft.client.gui.DrawContext;

import java.text.DecimalFormat;

/**
 * Компонент для отображения Slider настройки (double)
 * Тонкая белая линия с заполнением слева, значение справа
 */
public class SliderComponent extends SettingComponent<DoubleSetting> {
    private static final int COLOR_BG = 0xFF1E1E1E;           // Темный фон
    private static final int COLOR_BG_HOVER = 0xFF2A2A2A;     // Фон при наведении
    private static final int COLOR_SLIDER_BG = 0xFF333333;    // Фон слайдера
    private static final int COLOR_SLIDER_FILL = 0xFFAAAAAA;  // Заполнение слайдера
    private static final int COLOR_TEXT = 0xFFE0E0E0;         // Текст
    private static final int COLOR_VALUE = 0xFFFFFFFF;        // Значение

    private static final int SLIDER_HEIGHT = 3;
    private static final DecimalFormat df = new DecimalFormat("0.#");

    private final CustomFont font;
    private boolean dragging = false;

    public SliderComponent(DoubleSetting setting) {
        super(setting);
        this.font = CustomFont.getInstance();
        this.height = 22; // Немного выше для слайдера
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        // Фон компонента
        int bgColor = hovered ? COLOR_BG_HOVER : COLOR_BG;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Название настройки слева
        font.drawString(context, setting.getName(), x + 10, y + 4, COLOR_TEXT);

        // Значение справа вверху
        String valueText = df.format(setting.getValue());
        float valueWidth = font.getStringWidth(valueText);
        font.drawString(context, valueText, x + width - valueWidth - 10, y + 4, COLOR_VALUE);

        // Слайдер внизу
        float sliderY = y + height - 8;
        float sliderX = x + 10;
        float sliderWidth = width - 20;

        // Фон слайдера
        context.fill(
            (int) sliderX, 
            (int) sliderY, 
            (int) (sliderX + sliderWidth), 
            (int) (sliderY + SLIDER_HEIGHT), 
            COLOR_SLIDER_BG
        );

        // Заполнение слайдера
        double percentage = setting.getPercentage();
        float fillWidth = (float) (sliderWidth * percentage);
        
        context.fill(
            (int) sliderX, 
            (int) sliderY, 
            (int) (sliderX + fillWidth), 
            (int) (sliderY + SLIDER_HEIGHT), 
            COLOR_SLIDER_FILL
        );

        // Обработка перетаскивания
        if (dragging) {
            updateValue(mouseX);
        }

        // Тонкая линия-разделитель снизу
        context.fill((int) x, (int) (y + height - 1), (int) (x + width), (int) (y + height), 0xFF222222);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY)) {
            dragging = true;
            updateValue(mouseX);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging) {
            updateValue(mouseX);
        }
    }

    /**
     * Обновить значение слайдера на основе позиции мыши
     */
    private void updateValue(double mouseX) {
        float sliderX = x + 10;
        float sliderWidth = width - 20;
        
        double percentage = (mouseX - sliderX) / sliderWidth;
        percentage = Math.max(0, Math.min(1, percentage)); // Clamp 0-1
        
        setting.setPercentage(percentage);
    }
}
