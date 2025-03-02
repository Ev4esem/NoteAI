import torch
import whisper
import os
import platform

# Определение устройства
if torch.cuda.is_available():
    device = "cuda"
elif platform.system() == "Darwin":
    if torch.backends.mps.is_available() and platform.processor() == "arm":
        device = "mps"  # Для Mac на Apple Silicon
    else:
        device = "cpu"  # Для Mac на Intel
else:
    device = "cpu"

print(f"Используемое устройство: {device}")

# Проверка загрузки модели
try:
    model = whisper.load_model("base")
    print("Модель Whisper загружена успешно!")
except Exception as e:
    print(f"Ошибка при загрузке модели: {e}")

# Проверка наличия ffmpeg
if os.system("ffmpeg -version") == 0:
    print("FFmpeg установлен и доступен.")
else:
    print("FFmpeg не установлен или не доступен.")
