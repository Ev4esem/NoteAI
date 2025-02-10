from config import settings

import uvicorn
from fastapi import FastAPI, UploadFile, HTTPException, Request
from fastapi.responses import JSONResponse
from starlette.middleware.base import BaseHTTPMiddleware
import os

from fastapi.encoders import jsonable_encoder

# Создание папки для загрузки файлов, если ее нет
UPLOAD_DIR = "uploaded_files"
os.makedirs(UPLOAD_DIR, exist_ok=True)

app = FastAPI()

# Максимальный размер файла 100 МБ
MAX_FILE_SIZE = 100 * 1024 * 1024

# Пример middleware для ограничения количества запросов
class RateLimitMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # Для упрощения примера ограничим количество запросов на 5 в минуту
        if request.client.host == "127.0.0.1":  # Можно добавить логику для реального лимита
            pass
        response = await call_next(request)
        return response


app.add_middleware(RateLimitMiddleware)

@app.post("/upload/audio")
async def upload_audio(file: UploadFile):
    try:
        # Проверяем размер файла
        file_size = file.size
        if file_size > MAX_FILE_SIZE: raise HTTPException(status_code=413, detail="Аудио слишком большое (макс. 100 МБ)")

        # Сохраняем файл в директории
        file_location = os.path.join(UPLOAD_DIR, file.filename)
        with open(file_location, "wb") as f:
            f.write(await file.read())

        return JSONResponse(content={"message": "Аудиофайл успешно загружен"}, status_code=200)

    except HTTPException as e:
        raise e
    except Exception as e:
        # Любая другая ошибка
        raise HTTPException(status_code=500, detail="Ошибка на сервере. Мы уже работаем над этим")

@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    """Обработчик ошибок HTTPException для всех статусных кодов"""
    return JSONResponse(
        content=jsonable_encoder({"message": f"Ошибка клиента ({exc.status_code})"}),
        status_code=exc.status_code,
    )


@app.exception_handler(Exception)
async def generic_exception_handler(request: Request, exc: Exception):
    """Обработчик всех непредвиденных ошибок"""
    return JSONResponse(
        content=jsonable_encoder({"message": "Ошибка сервера"}),
        status_code=500,
    )

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=settings.PORT)

