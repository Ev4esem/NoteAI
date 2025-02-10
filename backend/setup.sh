#!/bin/bash

# Название виртуального окружения
VENV_NAME="venv"

# Проверка, существует ли файл requirements.txt
if [ ! -f "requirements.txt" ]; then
  echo "Не найден файл requirements.txt. Пожалуйста, убедитесь, что файл существует."
  exit 1
fi

# Создание виртуального окружения (если не существует)
if [ ! -d "$VENV_NAME" ]; then
  echo "Создание виртуального окружения..."
  python3 -m venv $VENV_NAME
else
  echo "Виртуальное окружение уже существует."
fi

# Активируем виртуальное окружение
echo "Активируем виртуальное окружение..."
source $VENV_NAME/bin/activate

# Установка зависимостей из requirements.txt
echo "Устанавливаем зависимости..."
pip install --upgrade pip
pip install -r requirements.txt

# Запуск FastAPI приложения с помощью uvicorn
echo "Запуск приложения FastAPI..."
python main.py

# Деактивация виртуального окружения после завершения работы
deactivate
