package tests

import (
	"noteai/config"
	"os"
	"testing"
)

func TestMain(m *testing.M) {
	// Загружаем .env один раз для всех тестов
	config.LoadLoading("../.env")

	code := m.Run() // Запуск всех тестов
	os.Exit(code)
}
