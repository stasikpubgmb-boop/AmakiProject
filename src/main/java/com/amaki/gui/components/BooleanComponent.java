package com.amaki.gui.components;

import com.amaki.gui.render.CustomFont;
import com.amaki.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

/**
 * Компонент для отображения Boolean настройки
 * Квадрат 12x12, заполнен белым если true
 */
public class BooleanComponent extends SettingComponent<BooleanSetting> {
    private static final int CHECKBOX_SIZE = 12;
    private static final int COLOR_BG = 0xFF1E1E1E;           // Темный фон
    private static final int COLOR_BG_HOVER = 0xFF2A2A2A;     // Фон при наведении
    private static final int COLOR_CHECKBOX = 0xFF333333;     // Граница чекбокса
    private static final int COLOR_CHECKED = 0xFFFFFFFF;      // Белый когда включен
    private static final int COLOR_TEXT = 0xFFE0E0E0;         // Светло-серый текст

    private final CustomFont font;

    public BooleanComponent(BooleanSetting setting) {
        super(setting);
        this.font = CustomFont.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        // Фон компонента
        int bgColor = hovered ? COLOR_BG_HOVER : COLOR_BG;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Название настройки слева
        font.drawString(context, setting.getName(), x + 10, y + (height - font.getFontHeight()) / 2, COLOR_TEXT);

        // Чекбокс справа
        float checkboxX = x + width - CHECKBOX_SIZE - 10;
        float checkboxY = y + (height - CHECKBOX_SIZE) / 2;

        // Граница чекбокса
        context.fill(
            (int) checkboxX, 
            (int) checkboxY, 
            (int) (checkboxX + CHECKBOX_SIZE), 
            (int) (checkboxY + CHECKBOX_SIZE), 
            COLOR_CHECKBOX
        );

        // Заполнение если включен
        if (setting.isEnabled()) {
            context.fill(
                (int) (checkboxX + 2), 
                (int) (checkboxY + 2), 
                (int) (checkboxX + CHECKBOX_SIZE - 2), 
                (int) (checkboxY + CHECKBOX_SIZE - 2), 
                COLOR_CHECKED
            );
        }

        // Тонкая линия-разделитель снизу
        context.fill((int) x, (int) (y + height - 1), (int) (x + width), (int) (y + height), 0xFF222222);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY)) {
            setting.toggle();
        }
    }
}
