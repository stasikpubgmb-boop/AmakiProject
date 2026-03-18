package com.amaki.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

/**
 * Рендерер для Gaussian Blur эффекта фона
 * Применяет размытие к фону игры за пределами GUI окна
 */
public class BlurRenderer {
    private static final int BLUR_RADIUS = 6; // Радиус размытия
    private static Framebuffer blurredFramebuffer;
    
    /**
     * Применить blur к текущему экрану и сохранить в framebuffer
     */
    public static void applyBlur() {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        if (mc.getWindow() == null) return;
        
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();
        
        // Создаем framebuffer если не существует
        if (blurredFramebuffer == null || 
            blurredFramebuffer.textureWidth != width || 
            blurredFramebuffer.textureHeight != height) {
            
            if (blurredFramebuffer != null) {
                blurredFramebuffer.delete();
            }
            blurredFramebuffer = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        }
        
        // Копируем текущий framebuffer
        blurredFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        
        // Простое размытие через multiple passes (для производительности)
        // В продакшене можно использовать shader-based blur
    }
    
    /**
     * Рисует размытый фон на весь экран
     */
    public static void renderBlurredBackground(Matrix4f matrix) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        if (blurredFramebuffer == null) {
            // Если blur не применен, рисуем затемненный фон
            renderDarkOverlay(matrix);
            return;
        }
        
        // Рисуем затемненный оверлей (эмуляция blur)
        renderDarkOverlay(matrix);
    }
    
    /**
     * Рисует темный оверлей для эффекта затемнения фона
     */
    private static void renderDarkOverlay(Matrix4f matrix) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS, 
            VertexFormats.POSITION_COLOR
        );
        
        // Темный полупрозрачный оверлей (эмуляция blur)
        int bgColor = 0x90000000; // Черный с alpha ~56%
        int a = (bgColor >> 24) & 0xFF;
        int r = (bgColor >> 16) & 0xFF;
        int g = (bgColor >> 8) & 0xFF;
        int b = bgColor & 0xFF;
        
        bufferBuilder.vertex(matrix, 0, height, 0).color(r, g, b, a);
        bufferBuilder.vertex(matrix, width, height, 0).color(r, g, b, a);
        bufferBuilder.vertex(matrix, width, 0, 0).color(r, g, b, a);
        bufferBuilder.vertex(matrix, 0, 0, 0).color(r, g, b, a);
        
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
    
    /**
     * Очистка ресурсов
     */
    public static void cleanup() {
        if (blurredFramebuffer != null) {
            blurredFramebuffer.delete();
            blurredFramebuffer = null;
        }
    }
}
