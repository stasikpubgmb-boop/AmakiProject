# Гайд по реализации Blur эффекта

## 🎨 Концепция

В премиум клиентах (Nursultan, Celestial, Expensive) blur работает так:
- **Фон Minecraft размыт** (Gaussian blur радиус 5-8)
- **Окно GUI НЕ размыто** - сплошной темный фон с alpha ~93-95%
- Создается эффект "глубины" и фокуса на GUI

## 📊 Текущая реализация (v1.0.0)

### `BlurRenderer.java` - Базовая версия

```java
public static void renderBlurredBackground(Matrix4f matrix) {
    // Рисуем темный полупрозрачный оверлей
    // Это эмулирует blur эффект через затемнение
    renderDarkOverlay(matrix);
}

private static void renderDarkOverlay(Matrix4f matrix) {
    int bgColor = 0x90000000; // Черный с alpha ~56%
    
    // Рендерим полноэкранный quad
    BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
        VertexFormat.DrawMode.QUADS, 
        VertexFormats.POSITION_COLOR
    );
    
    // ... vertices ...
    
    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
}
```

**Плюсы:**
- ✅ Простая реализация
- ✅ Высокая производительность
- ✅ Работает на любом железе

**Минусы:**
- ❌ Не настоящий blur, а просто затемнение
- ❌ Менее визуально впечатляющий

## 🔥 Продвинутая реализация - Gaussian Blur

### Метод 1: Framebuffer + Dual-pass Blur

Используем классический подход с двумя проходами (horizontal + vertical).

```java
public class AdvancedBlurRenderer {
    private static Framebuffer inputFbo;
    private static Framebuffer outputFbo;
    private static Framebuffer horizontalFbo;
    
    private static final int BLUR_RADIUS = 6;
    
    /**
     * Захват и размытие текущего экрана
     */
    public static void captureAndBlur() {
        MinecraftClient mc = MinecraftClient.getInstance();
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();
        
        // Создаем framebuffers
        initFramebuffers(width, height);
        
        // 1. Копируем главный framebuffer
        Framebuffer mainFbo = mc.getFramebuffer();
        inputFbo.copyDepthFrom(mainFbo);
        
        // 2. Horizontal blur pass
        horizontalFbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        horizontalFbo.beginWrite(false);
        applyBlurPass(inputFbo, true); // horizontal
        horizontalFbo.endWrite();
        
        // 3. Vertical blur pass
        outputFbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        outputFbo.beginWrite(false);
        applyBlurPass(horizontalFbo, false); // vertical
        outputFbo.endWrite();
        
        // Возвращаемся к главному framebuffer
        mainFbo.beginWrite(false);
    }
    
    /**
     * Применить blur pass (horizontal или vertical)
     */
    private static void applyBlurPass(Framebuffer source, boolean horizontal) {
        // Используем simple box blur для производительности
        // Можно заменить на Gaussian kernel
        
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, source.getColorAttachment());
        
        // Box blur kernel
        int kernelSize = BLUR_RADIUS * 2 + 1;
        float offset = 1.0f / (horizontal ? source.textureWidth : source.textureHeight);
        
        // Рендерим размытую текстуру
        Matrix4f matrix = new Matrix4f().setOrtho(
            0, source.textureWidth, 
            source.textureHeight, 0, 
            1000, 3000
        );
        
        RenderSystem.setProjectionMatrix(matrix, VertexSorter.BY_Z);
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS,
            VertexFormats.POSITION_TEXTURE
        );
        
        bufferBuilder.vertex(0, source.textureHeight, 0).texture(0, 0);
        bufferBuilder.vertex(source.textureWidth, source.textureHeight, 0).texture(1, 0);
        bufferBuilder.vertex(source.textureWidth, 0, 0).texture(1, 1);
        bufferBuilder.vertex(0, 0, 0).texture(0, 1);
        
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
    
    /**
     * Рендер размытого фона
     */
    public static void renderBlurred(Matrix4f matrix) {
        if (outputFbo == null) return;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, outputFbo.getColorAttachment());
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS,
            VertexFormats.POSITION_TEXTURE
        );
        
        MinecraftClient mc = MinecraftClient.getInstance();
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        
        bufferBuilder.vertex(matrix, 0, height, 0).texture(0, 0);
        bufferBuilder.vertex(matrix, width, height, 0).texture(1, 0);
        bufferBuilder.vertex(matrix, width, 0, 0).texture(1, 1);
        bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 1);
        
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
    
    private static void initFramebuffers(int width, int height) {
        if (inputFbo == null || inputFbo.textureWidth != width) {
            if (inputFbo != null) inputFbo.delete();
            if (outputFbo != null) outputFbo.delete();
            if (horizontalFbo != null) horizontalFbo.delete();
            
            inputFbo = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
            outputFbo = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
            horizontalFbo = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        }
    }
}
```

### Метод 2: Custom Shader (самый профессиональный)

Создаем кастомный GLSL shader для Gaussian blur.

#### 1. Создать shader файлы

`assets/amakiproject/shaders/program/blur.json`:
```json
{
  "blend": {
    "func": "add",
    "srcrgb": "one",
    "dstrgb": "zero"
  },
  "vertex": "amakiproject:blur",
  "fragment": "amakiproject:blur",
  "attributes": [
    "Position",
    "UV0"
  ],
  "samplers": [
    { "name": "InSampler" }
  ],
  "uniforms": [
    { "name": "ProjMat", "type": "matrix4x4", "count": 16, "values": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },
    { "name": "InSize", "type": "float", "count": 2, "values": [ 1.0, 1.0 ] },
    { "name": "BlurDir", "type": "float", "count": 2, "values": [ 1.0, 0.0 ] },
    { "name": "Radius", "type": "int", "count": 1, "values": [ 6 ] }
  ]
}
```

`assets/amakiproject/shaders/core/blur.vsh`:
```glsl
#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ProjMat;

out vec2 texCoord;

void main() {
    gl_Position = ProjMat * vec4(Position, 1.0);
    texCoord = UV0;
}
```

`assets/amakiproject/shaders/core/blur.fsh`:
```glsl
#version 150

uniform sampler2D InSampler;
uniform vec2 InSize;
uniform vec2 BlurDir;
uniform int Radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 blurred = vec4(0.0);
    float totalWeight = 0.0;
    
    // Gaussian kernel
    for (int i = -Radius; i <= Radius; i++) {
        float weight = exp(-float(i * i) / (2.0 * float(Radius * Radius)));
        vec2 offset = BlurDir * float(i) / InSize;
        blurred += texture(InSampler, texCoord + offset) * weight;
        totalWeight += weight;
    }
    
    fragColor = blurred / totalWeight;
}
```

#### 2. Использование в коде

```java
public class ShaderBlurRenderer {
    private static ShaderEffect blurShader;
    
    public static void loadShader() {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            blurShader = new ShaderEffect(
                mc.getTextureManager(),
                mc.getResourceManager(),
                mc.getFramebuffer(),
                Identifier.of("amakiproject", "shaders/program/blur.json")
            );
            blurShader.setupDimensions(
                mc.getWindow().getFramebufferWidth(),
                mc.getWindow().getFramebufferHeight()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void applyBlur() {
        if (blurShader != null) {
            blurShader.render(MinecraftClient.getInstance().getTickDelta());
        }
    }
}
```

## 📈 Сравнение методов

| Метод | Производительность | Качество | Сложность |
|-------|-------------------|----------|-----------|
| Dark Overlay (текущий) | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐ |
| Dual-pass Blur | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Custom Shader | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

## 🎯 Рекомендации

### Для оптимальной производительности:
1. Используйте **downsampling** (рендерить blur на меньшем разрешении)
2. Кэшируйте размытый framebuffer (не пересчитывать каждый кадр)
3. Используйте **separable Gaussian kernel** (horizontal + vertical passes)

### Пример оптимизации:

```java
public class OptimizedBlurRenderer {
    private static long lastBlurTime = 0;
    private static final long BLUR_CACHE_TIME = 100; // ms
    
    public static void renderBlurred(Matrix4f matrix) {
        long currentTime = System.currentTimeMillis();
        
        // Обновляем blur только раз в 100ms
        if (currentTime - lastBlurTime > BLUR_CACHE_TIME) {
            captureAndBlur();
            lastBlurTime = currentTime;
        }
        
        // Рендерим закэшированный blur
        renderCachedBlur(matrix);
    }
}
```

## 🔗 Интеграция в ClickGuiScreen

Замените в `ClickGuiScreen.render()`:

```java
@Override
public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    // БЫЛО:
    // BlurRenderer.renderBlurredBackground(context.getMatrices().peek().getPositionMatrix());
    
    // СТАЛО (выберите один из методов):
    
    // Метод 1: Dark Overlay (текущий)
    BlurRenderer.renderBlurredBackground(context.getMatrices().peek().getPositionMatrix());
    
    // Метод 2: Dual-pass Blur
    // AdvancedBlurRenderer.captureAndBlur();
    // AdvancedBlurRenderer.renderBlurred(context.getMatrices().peek().getPositionMatrix());
    
    // Метод 3: Custom Shader
    // ShaderBlurRenderer.applyBlur();
    
    renderWindow(context, mouseX, mouseY, delta);
    super.render(context, mouseX, mouseY, delta);
}
```

## 💡 Советы по отладке

1. **Проверить framebuffer**:
```java
System.out.println("FBO size: " + fbo.textureWidth + "x" + fbo.textureHeight);
```

2. **Визуализировать blur passes**:
```java
// Рендерить horizontal pass отдельно для проверки
horizontalFbo.draw(width, height);
```

3. **Профилирование**:
```java
long start = System.nanoTime();
applyBlur();
long end = System.nanoTime();
System.out.println("Blur time: " + (end - start) / 1_000_000.0 + "ms");
```

---

**Рекомендация для AmakiProject v1.0.0:**  
Оставить текущую реализацию (Dark Overlay) как default, добавить опцию в конфиге для включения Advanced Blur для мощных ПК.

```java
// В будущей версии:
public enum BlurMode {
    NONE,           // Без эффекта
    DARK_OVERLAY,   // Текущая (быстрая)
    GAUSSIAN_BLUR   // Продвинутая (красивая)
}
```
