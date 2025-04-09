package config

import (
	"github.com/joho/godotenv"
	"log"
	"os"
	"reflect"
)

type Config struct {
	PORT    string
	CHATGPT string

	PROXY_URL string

	KAFKA_HOST string
}

var AppConfig Config

func LoadLoading(pathEnv string) {
	err := godotenv.Load(pathEnv)
	if err != nil {
		log.Fatal("Error loading .env file")
		return
	}

	v := reflect.ValueOf(&AppConfig).Elem()
	t := v.Type()

	for i := 0; i < v.NumField(); i++ {
		field := t.Field(i)
		envName := field.Name // можно сделать кастомный маппинг, если нужно

		value := os.Getenv(envName)
		if value == "" {
			log.Printf("⚠️  Переменная окружения %s не найдена", envName)
		}
		v.Field(i).SetString(value)
	}
}
