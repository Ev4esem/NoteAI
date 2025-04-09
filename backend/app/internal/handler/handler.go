package handler

import (
	"encoding/json"
	"net/http"
	"noteai/internal/gpt"
)

// Обработчик для получения запроса от клиента и взаимодействия с ChatGPT
func handler(w http.ResponseWriter, r *http.Request) {
	// Получаем данные от клиента
	r.ParseForm()
	prompt := r.FormValue("prompt")

	// Если prompt не передан, отправляем ошибку
	if prompt == "" {
		http.Error(w, "Prompt is required", http.StatusBadRequest)
		return
	}

	// Отправляем запрос к ChatGPT и получаем ответ
	response, err := gpt.callChatGPT("123",
		"Write a one-sentence bedtime story about a unicorn.",
		"As the moonlight danced on the meadow, the little unicorn whispered secrets to the stars, dreaming of adventures yet to come.")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Отправляем результат клиенту
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"response": response})
}
