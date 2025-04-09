package main

import (
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"log"
	"net/http"
	"noteai/config"
	"noteai/internal/api"
	"noteai/internal/kafka"
	"noteai/storage"
)

func init() {
	config.LoadLoading(".env")

	log.Println("kafka port: " + config.AppConfig.KAFKA_HOST)

	err := storage.InitDB("./tasks.db")
	if err != nil {
		log.Fatalf("init db err: %v", err)
	}

	kafka.InitTopic()
	go kafka.Consumer()

	log.Println("Consumer kafka started")
}

func main() {

	server := &http.Server{
		Addr:    ":" + config.AppConfig.PORT,
		Handler: setupRouter(),
	}

	log.Printf("Server is running on http://localhost:%v", config.AppConfig.PORT)
	server.ListenAndServe()
}

func setupRouter() *chi.Mux {
	router := chi.NewRouter()
	router.Use(middleware.Logger)
	router.Get("/", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("Hello World!"))
	})

	api.AudioRouters(router)
	api.TasksRouters(router)

	return router
}
