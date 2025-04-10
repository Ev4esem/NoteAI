package api

import (
	"encoding/json"
	"github.com/go-chi/chi/v5"
	"io"
	"log"
	"net/http"
	"noteai/storage"
)

func TasksRouters(router chi.Router) {
	router.Post("/tasks", tasks)
	router.Get("/tasks", allTasks)
}

func tasks(w http.ResponseWriter, r *http.Request) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
	}
	var tasks []string
	err = json.Unmarshal(body, &tasks)
	if err != nil {
		http.Error(w, "Ошибка разбора JSON: "+err.Error(), http.StatusBadRequest)
		return
	}

	// Вывод разобранного массива в лог
	log.Printf("Получен массив: %+v", tasks)

	resTasks, err := storage.GetTasksByIDs(tasks)
	if err != nil {
		http.Error(w, "Произошла ошибка при попытке поолучить список по айди: "+err.Error(), http.StatusBadRequest)
		return
	}

	for _, task := range resTasks {
		if task.State == "ready" {
			storage.DeleteTask(task.ID)
		}
	}

	respJson, err := json.Marshal(resTasks)
	if err != nil {
		http.Error(w, "Error marshall Json", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	_, errWrite := w.Write(respJson)
	if errWrite != nil {
		http.Error(w, "Ошибка записи ответа "+errWrite.Error(), http.StatusInternalServerError)
		return
	}
}

func allTasks(w http.ResponseWriter, r *http.Request) {

	tasks, err := storage.GetAllTasks()
	if err != nil {
		http.Error(w, "Не удалось вытащить весь список тасков: "+err.Error(), http.StatusBadRequest)
	}

	respJson, err := json.Marshal(tasks)
	if err != nil {
		http.Error(w, "Error marshall Json", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	_, errWrite := w.Write(respJson)
	if errWrite != nil {
		http.Error(w, "Ошибка записи ответа "+errWrite.Error(), http.StatusInternalServerError)
		return
	}
}
