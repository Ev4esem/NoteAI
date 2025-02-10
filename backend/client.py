from config import settings
import requests

# URL сервера
SERVER_URL = f"http://localhost:{settings.PORT}/upload/audio"

# Файл, который будем отправлять
directory = "./static"  # Путь к файлу на клиенте
file_name = "audio.mp3"

# Открытие файла
with open(f"{directory}/{file_name}", "rb") as audio_file:
    files = {'file': (file_name, audio_file)}
    try:
        # Отправка POST-запроса с файлом
        response = requests.post(SERVER_URL, files=files)

        # Обработка ответа
        if response.status_code == 200:
            print("Аудиофайл успешно загружен")
        elif response.status_code == 400:
            print("Некорректный запрос")
        elif response.status_code == 413:
            print("Аудио слишком большое (макс. 100 МБ)")
        elif response.status_code == 429:
            print("Слишком много запросов. Попробуйте позже")
        elif 500 <= response.status_code <= 504:
            print("Ошибка на сервере. Мы уже работаем над этим")
        elif 400 <= response.status_code <= 499:
            print(f"Ошибка клиента ({response.status_code})")
        elif 500 <= response.status_code <= 599:
            print(f"Ошибка сервера ({response.status_code})")
        else:
            print("Неизвестная ошибка")

    except requests.exceptions.RequestException as e:
        print(f"Ошибка при подключении к серверу: {e}")
