package main

import (
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"log"
	"net/http"
	"noteai/config"
	"noteai/internal/api"
	"noteai/internal/kafka"
	"noteai/internal/miniio"
	"noteai/storage"
)

func init() {
	config.LoadLoading(".env")

	err := storage.InitDB("./tasks.db")
	if err != nil {
		log.Fatalf("init db err: %v", err)
	}

	// minio
	miniio.StartMinIO()

	// kafka
	kafka.InitTopic()
	go kafka.Consumer()

	log.Println("Consumer kafka started")
}

func main() {

	server := &http.Server{
		Addr:    ":" + config.AppConfig.PORT,
		Handler: setupRouter(),
	}

	log.Printf("Server will run on http://localhost:%v", config.AppConfig.PORT)
	err := server.ListenAndServe()
	if err != nil {
		log.Fatal("server err: ", err)
	}
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
