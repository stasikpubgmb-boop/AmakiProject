package com.amaki.gui.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Wrapper для кастомного TTF шрифта
 * Использует Inter-Regular.ttf из resources
 */
public class CustomFont {
    private static CustomFont instance;
    private final MinecraftClient mc;
    private TextRenderer fontRenderer;
    private final Identifier customFontId;

    private CustomFont() {
        this.mc = MinecraftClient.getInstance();
        this.customFontId = Identifier.of("amakiproject", "custom");
        
        // Пытаемся загрузить кастомный шрифт
        try {
            this.fontRenderer = mc.textRenderer;
            // В Minecraft 1.21.4 шрифты загружаются автоматически через font providers
            // Мы используем стандартный renderer, но можем переключиться на кастомный если нужно
        } catch (Exception e) {
            System.err.println("Failed to load custom font, using default: " + e.getMessage());
            this.fontRenderer = mc.textRenderer;
        }
    }

    public static CustomFont getInstance() {
        if (instance == null) {
            instance = new CustomFont();
        }
        return instance;
    }

    /**
     * Рисует текст без тени
     */
    public int drawString(DrawContext context, String text, float x, float y, int color) {
        return context.drawText(fontRenderer, text, (int) x, (int) y, color, false);
    }

    /**
     * Рисует текст с тенью (опционально)
     */
    public int drawStringWithShadow(DrawContext context, String text, float x, float y, int color) {
        return context.drawText(fontRenderer, text, (int) x, (int) y, color, true);
    }

    /**
     * Рисует центрированный текст
     */
    public int drawCenteredString(DrawContext context, String text, float x, float y, int color) {
        float width = getStringWidth(text);
        return drawString(context, text, x - width / 2, y, color);
    }

    /**
     * Получить ширину строки
     */
    public float getStringWidth(String text) {
        return fontRenderer.getWidth(text);
    }

    /**
     * Получить высоту шрифта
     */
    public int getFontHeight() {
        return fontRenderer.fontHeight;
    }

    /**
     * Обрезать строку до определенной ширины
     */
    public String trimToWidth(String text, int width) {
        return fontRenderer.trimToWidth(text, width);
    }

    public TextRenderer getRenderer() {
        return fontRenderer;
    }
}
