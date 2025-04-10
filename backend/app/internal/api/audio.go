package api

import (
	"encoding/json"
	"github.com/go-chi/chi/v5"
	"github.com/google/uuid"
	"log"
	"net/http"
	"noteai/internal/kafka"
	"noteai/internal/miniio"
	"noteai/storage"
	"path/filepath"
	"time"
)

const maxFileSize = 100 * 1024 * 1024 // 100 MB

func AudioRouters(router chi.Router) {
	router.Post("/audio", processAudio)
}

func processAudio(w http.ResponseWriter, r *http.Request) {
	// Ограничиваем размер запроса
	r.Body = http.MaxBytesReader(w, r.Body, maxFileSize)

	// Парсим форму с файлами
	err := r.ParseMultipartForm(maxFileSize)
	if err != nil {
		http.Error(w, "Аудио слишком большое (макс. 100 МБ)", http.StatusRequestEntityTooLarge)
		return
	}

	// Получаем файл из формы
	file, fileHeader, err := r.FormFile("file")
	if err != nil {
		http.Error(w, "Ошибка получения файла", http.StatusBadRequest)
		return
	}
	defer file.Close()

	newAudioId := uuid.New()
	ext := filepath.Ext(fileHeader.Filename)
	if ext == "" {
		ext = ".mp3" // fallback по умолчанию
	}
	objectName := newAudioId.String() + ext

	// Загружаем файл в MinIO
	uploadInfo, err := miniio.UploadToMinIO(file, fileHeader, objectName)
	if err != nil {
		http.Error(w, "Ошибка загрузки в MinIO: "+err.Error(), http.StatusInternalServerError)
		return
	}
	log.Println("Загружен файл в MinIO:", uploadInfo)

	// Сохраняем задачу в БД
	errAddTask := storage.AddTask(newAudioId.String())
	if errAddTask != nil {
		http.Error(w, "Ошибка записи задачи "+errAddTask.Error(), http.StatusInternalServerError)
		return
	}

	// Отправляем UUID в Kafka запись в брокер сообщений
	var kafkaMess kafka.Message
	kafkaMess.Key = []byte(newAudioId.String())
	kafkaMess.Value = []byte(ext)
	kafka.Producer(kafkaMess)

	response := map[string]string{
		"message":  "Аудиофайл загружен и уже обрабатывается",
		"audio_id": newAudioId.String(),
		"status":   "wait",
	}
	jsonResponse, _ := json.Marshal(response)

	// Запись ответа
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	_, errWrite := w.Write(jsonResponse)
	if errWrite != nil {
		http.Error(w, "Ошибка записи ответа "+errWrite.Error(), http.StatusInternalServerError)
		return
	}

	time.Sleep(2 * time.Second)
}
