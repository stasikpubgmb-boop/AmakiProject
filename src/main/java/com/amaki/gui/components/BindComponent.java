package com.amaki.gui.components;

import com.amaki.gui.render.CustomFont;
import com.amaki.module.setting.KeybindSetting;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

/**
 * Компонент для отображения Keybind настройки
 * Текст "Keybind" + название клавиши (серый если none)
 */
public class BindComponent extends SettingComponent<KeybindSetting> {
    private static final int COLOR_BG = 0xFF1E1E1E;           // Темный фон
    private static final int COLOR_BG_HOVER = 0xFF2A2A2A;     // Фон при наведении
    private static final int COLOR_TEXT = 0xFFE0E0E0;         // Текст
    private static final int COLOR_KEY = 0xFFAAAAAA;          // Название клавиши
    private static final int COLOR_KEY_NONE = 0xFF666666;     // Серый если нет клавиши
    private static final int COLOR_LISTENING = 0xFF4A9EFF;    // Синий когда слушаем

    private final CustomFont font;
    private boolean listening = false;

    public BindComponent(KeybindSetting setting) {
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

        // Название клавиши справа
        String keyText = listening ? "..." : setting.getKeyName();
        int keyColor = listening ? COLOR_LISTENING : 
                      (setting.getKey() == -1 ? COLOR_KEY_NONE : COLOR_KEY);
        
        float keyWidth = font.getStringWidth(keyText);
        font.drawString(context, keyText, x + width - keyWidth - 10, 
                       y + (height - font.getFontHeight()) / 2, keyColor);

        // Тонкая линия-разделитель снизу
        context.fill((int) x, (int) (y + height - 1), (int) (x + width), (int) (y + height), 0xFF222222);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY)) {
            listening = !listening;
        }
    }

    /**
     * Обработка нажатия клавиши для биндинга
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!listening) {
            return false;
        }

        // Escape - отменить
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            listening = false;
            return true;
        }

        // Delete/Backspace - убрать бинд
        if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            setting.setKey(-1);
            listening = false;
            return true;
        }

        // Установить новый бинд
        setting.setKey(keyCode);
        listening = false;
        return true;
    }

    public boolean isListening() {
        return listening;
    }

    public void stopListening() {
        listening = false;
    }
}
