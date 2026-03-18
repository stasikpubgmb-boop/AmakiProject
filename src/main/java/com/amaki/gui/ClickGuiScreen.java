package com.amaki.gui;

import com.amaki.gui.components.BindComponent;
import com.amaki.gui.render.BlurRenderer;
import com.amaki.gui.render.CustomFont;
import com.amaki.module.Module;
import com.amaki.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Премиум ClickGUI в стиле Nursultan/Celestial/Expensive
 * - Blur фона за пределами GUI
 * - Темный сплошной фон окна (#0D0D0D, alpha 0.93)
 * - Верхняя панель категорий с вкладками
 * - Список модулей с настройками
 * - Кастомный TTF шрифт
 * - Плавные анимации
 */
public class ClickGuiScreen extends Screen {
    
    // Размеры и позиция GUI
    private static final int GUI_WIDTH = 460;
    private static final int GUI_HEIGHT = 380;
    private int guiX, guiY;
    
    // Цвета
    private static final int COLOR_WINDOW_BG = 0xEE0D0D0D;      // Темный фон окна (alpha ~93%)
    private static final int COLOR_TAB_BAR = 0xFF111111;        // Фон панели вкладок
    private static final int COLOR_TAB_ACTIVE = 0x1FFFFFFF;     // Активная вкладка (alpha ~12%)
    private static final int COLOR_TAB_INACTIVE = 0xFF222222;   // Неактивная вкладка
    private static final int COLOR_TEXT = 0xFFFFFFFF;           // Белый текст
    private static final int COLOR_TEXT_DIM = 0xFFAAAAAA;       // Тусклый текст
    
    // Параметры верхней панели
    private static final int TAB_BAR_HEIGHT = 34;
    private static final int TAB_PADDING = 12;
    private static final int TAB_SPACING = 8;
    private static final int CORNER_RADIUS = 5;
    
    // Параметры скроллинга
    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private static final float SCROLL_SPEED = 20f;
    
    // Анимация fade
    private float fadeAlpha = 0f;
    private static final float FADE_SPEED = 0.08f;
    
    private final CustomFont font;
    private Category selectedCategory = Category.COMBAT;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    
    public ClickGuiScreen() {
        super(Text.literal("ClickGUI"));
        this.font = CustomFont.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        
        // Центрируем GUI
        guiX = (width - GUI_WIDTH) / 2;
        guiY = (height - GUI_HEIGHT) / 2;
        
        // Загружаем модули выбранной категории
        loadCategory(selectedCategory);
    }

    /**
     * Загрузить модули выбранной категории
     */
    private void loadCategory(Category category) {
        moduleButtons.clear();
        scrollOffset = 0;
        targetScrollOffset = 0;
        
        List<Module> modules = ModuleManager.getInstance().getModulesByCategory(category);
        for (Module module : modules) {
            moduleButtons.add(new ModuleButton(module));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Обновляем fade анимацию
        if (fadeAlpha < 1f) {
            fadeAlpha = Math.min(1f, fadeAlpha + FADE_SPEED);
        }
        
        // Рисуем размытый фон
        BlurRenderer.renderBlurredBackground(context.getMatrices().peek().getPositionMatrix());
        
        // Применяем fade alpha
        int fadeColor = ((int)(fadeAlpha * 255) << 24) | 0xFFFFFF;
        
        // Основное окно GUI
        renderWindow(context, mouseX, mouseY, delta);
        
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * Отрисовка основного окна GUI
     */
    private void renderWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        // Фон окна (сплошной темный без blur внутри)
        context.fill(guiX, guiY, guiX + GUI_WIDTH, guiY + GUI_HEIGHT, COLOR_WINDOW_BG);
        
        // Верхняя панель категорий
        renderTabBar(context, mouseX, mouseY);
        
        // Список модулей
        renderModuleList(context, mouseX, mouseY, delta);
    }

    /**
     * Отрисовка панели вкладок категорий
     */
    private void renderTabBar(DrawContext context, int mouseX, int mouseY) {
        int tabBarY = guiY;
        
        // Фон панели вкладок
        context.fill(guiX, tabBarY, guiX + GUI_WIDTH, tabBarY + TAB_BAR_HEIGHT, COLOR_TAB_BAR);
        
        int currentX = guiX + 10;
        
        // Иконка мода слева [A]
        int iconSize = 16;
        int iconY = tabBarY + (TAB_BAR_HEIGHT - iconSize) / 2;
        context.fill(currentX, iconY, currentX + iconSize, iconY + iconSize, 0xFFFFFFFF);
        context.fill(currentX + 1, iconY + 1, currentX + iconSize - 1, iconY + iconSize - 1, COLOR_TAB_BAR);
        
        // Текст "A" в иконке
        font.drawString(context, "A", currentX + 4, iconY + 4, COLOR_TEXT);
        currentX += iconSize + 15;
        
        // Вкладки категорий
        for (Category category : Category.values()) {
            boolean isActive = category == selectedCategory;
            String tabText = category.getName();
            int tabWidth = (int) font.getStringWidth(tabText) + TAB_PADDING * 2;
            
            // Проверяем наведение
            boolean tabHovered = mouseX >= currentX && mouseX <= currentX + tabWidth &&
                                mouseY >= tabBarY && mouseY <= tabBarY + TAB_BAR_HEIGHT;
            
            // Фон вкладки (закругленный сверху)
            int tabColor = isActive ? COLOR_TAB_ACTIVE : COLOR_TAB_INACTIVE;
            if (tabHovered && !isActive) {
                tabColor = 0xFF2A2A2A; // Чуть светлее при наведении
            }
            
            int tabY = tabBarY + 6;
            int tabHeight = TAB_BAR_HEIGHT - 6;
            
            // Рисуем вкладку
            context.fill(currentX, tabY, currentX + tabWidth, tabY + tabHeight, tabColor);
            
            // Текст вкладки
            int textColor = isActive ? COLOR_TEXT : COLOR_TEXT_DIM;
            font.drawString(context, tabText, currentX + TAB_PADDING, 
                          tabY + (tabHeight - font.getFontHeight()) / 2, textColor);
            
            // Иконка справа (только для активной)
            if (isActive) {
                String icon = "✗";
                float iconWidth = font.getStringWidth(icon);
                font.drawString(context, icon, currentX + tabWidth - iconWidth - 8, 
                              tabY + (tabHeight - font.getFontHeight()) / 2, COLOR_TEXT);
            } else {
                // Иконка категории для неактивных
                String icon = category.getIcon();
                float iconWidth = font.getStringWidth(icon);
                font.drawString(context, icon, currentX + tabWidth - iconWidth - 8, 
                              tabY + (tabHeight - font.getFontHeight()) / 2, COLOR_TEXT_DIM);
            }
            
            currentX += tabWidth + TAB_SPACING;
        }
        
        // Нижняя линия панели
        context.fill(guiX, tabBarY + TAB_BAR_HEIGHT - 1, 
                    guiX + GUI_WIDTH, tabBarY + TAB_BAR_HEIGHT, 0xFF222222);
    }

    /**
     * Отрисовка списка модулей
     */
    private void renderModuleList(DrawContext context, int mouseX, int mouseY, float delta) {
        int listY = guiY + TAB_BAR_HEIGHT;
        int listHeight = GUI_HEIGHT - TAB_BAR_HEIGHT;
        
        // Плавный скроллинг
        scrollOffset += (targetScrollOffset - scrollOffset) * 0.3f;
        
        // Enable scissor для обрезки модулей
        context.enableScissor(guiX, listY, guiX + GUI_WIDTH, listY + listHeight);
        
        float currentY = listY - scrollOffset;
        
        for (ModuleButton button : moduleButtons) {
            button.setPosition(guiX, currentY, GUI_WIDTH);
            button.render(context, mouseX, mouseY, delta);
            currentY += button.getTotalHeight();
        }
        
        context.disableScissor();
        
        // Обновляем максимальный скролл
        float totalHeight = getTotalModulesHeight();
        float maxScroll = Math.max(0, totalHeight - listHeight);
        targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
    }

    /**
     * Получить общую высоту всех модулей
     */
    private float getTotalModulesHeight() {
        float height = 0;
        for (ModuleButton button : moduleButtons) {
            height += button.getTotalHeight();
        }
        return height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем клик по вкладкам
        int tabBarY = guiY;
        int currentX = guiX + 10 + 16 + 15; // После иконки
        
        for (Category category : Category.values()) {
            String tabText = category.getName();
            int tabWidth = (int) font.getStringWidth(tabText) + TAB_PADDING * 2;
            
            if (mouseX >= currentX && mouseX <= currentX + tabWidth &&
                mouseY >= tabBarY && mouseY <= tabBarY + TAB_BAR_HEIGHT) {
                
                if (selectedCategory != category) {
                    selectedCategory = category;
                    loadCategory(category);
                }
                return true;
            }
            
            currentX += tabWidth + TAB_SPACING;
        }
        
        // Проверяем клик по модулям
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.mouseClicked(mouseX, mouseY, button);
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Скроллинг списка модулей
        int listY = guiY + TAB_BAR_HEIGHT;
        int listHeight = GUI_HEIGHT - TAB_BAR_HEIGHT;
        
        if (mouseY >= listY && mouseY <= listY + listHeight) {
            targetScrollOffset -= (float) verticalAmount * SCROLL_SPEED;
            targetScrollOffset = Math.max(0, targetScrollOffset);
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Закрытие на Escape
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        
        // Передаем нажатие клавиш компонентам биндов
        for (ModuleButton button : moduleButtons) {
            if (button.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        fadeAlpha = 0f; // Сброс анимации для следующего открытия
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false; // Не ставим игру на паузу
    }
}
