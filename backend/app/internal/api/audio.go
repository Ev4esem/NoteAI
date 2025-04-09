package api

import (
	"encoding/json"
	"github.com/go-chi/chi/v5"
	"github.com/google/uuid"
	"io"
	"net/http"
	"noteai/internal/kafka"
	"noteai/storage"
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
	file, _, err := r.FormFile("file")
	if err != nil {
		http.Error(w, "Ошибка получения файла", http.StatusBadRequest)
		return
	}

	contentFile, errReadFile := io.ReadAll(file)
	if errReadFile != nil {
		http.Error(w, "Ошибка при попытке прочитать файл"+errReadFile.Error(), http.StatusBadRequest)
	}

	newAudioId := uuid.New()

	response := map[string]string{
		"message":  "Аудиофайл загружен и уже обрабатывается",
		"audio_id": newAudioId.String(),
		"status":   "wait",
	}
	jsonResponse, errMarsh := json.Marshal(response)
	if errMarsh != nil {
		http.Error(w, "Произошла непредвиденная ошибка "+errMarsh.Error(), http.StatusInternalServerError)
		return
	}

	// Запись ответа
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	_, errWrite := w.Write(jsonResponse)
	if errWrite != nil {
		http.Error(w, "Ошибка записи ответа "+errWrite.Error(), http.StatusInternalServerError)
		return
	}

	errAddTask := storage.AddTask(newAudioId.String())
	if errAddTask != nil {
		http.Error(w, "Ошибка записи задачи "+errWrite.Error(), http.StatusInternalServerError)
		return
	}

	// запись в брокер сообщений
	var kafkaMess kafka.Message
	kafkaMessKey := []byte(newAudioId.String())
	kafkaMess.Key = kafkaMessKey
	kafkaMess.Value = contentFile
	kafka.Producer(kafkaMess)

	time.Sleep(2 * time.Second)
}
