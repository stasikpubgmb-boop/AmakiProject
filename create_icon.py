from PIL import Image, ImageDraw, ImageFont
import os

# Создаем иконку 128x128
size = 128
img = Image.new('RGB', (size, size), color='#0D0D0D')
draw = ImageDraw.Draw(img)

# Рисуем белую рамку
border = 8
draw.rectangle([border, border, size-border, size-border], outline='#FFFFFF', width=3)

# Рисуем букву "A" в центре
try:
    # Пытаемся использовать системный шрифт
    font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 80)
except:
    font = ImageFont.load_default()

text = "A"
bbox = draw.textbbox((0, 0), text, font=font)
text_width = bbox[2] - bbox[0]
text_height = bbox[3] - bbox[1]
position = ((size - text_width) // 2, (size - text_height) // 2 - 10)

draw.text(position, text, fill='#FFFFFF', font=font)

# Сохраняем
output_path = '/root/AmakiProject/src/main/resources/assets/amakiproject/icon.png'
os.makedirs(os.path.dirname(output_path), exist_ok=True)
img.save(output_path)
print(f"Icon created: {output_path}")
