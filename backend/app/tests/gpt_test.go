package tests

import (
	"log"
	"noteai/config"
	"noteai/internal/gpt"
	"os"
	"testing"
)

func TestCallChatGPT(t *testing.T) {

	response, err := gpt.CallChatGPT("Write a one-sentence bedtime story about a unicorn.")

	if err != nil {
		t.Fatalf("Error calling ChatGPT: %v", err)
	}

	if response == "" {
		t.Error("Expected a non-empty response from ChatGPT")
	}

	t.Logf("Response from ChatGPT: %s", response)
}

func TestCallChatGPTWhisper(t *testing.T) {
	// Открываем файл
	file, err := os.ReadFile("./static/audio.mp3")
	if err != nil {
		log.Fatalf("Ошибка чтения файл: %v", err)
		return
	}

	resp, err := gpt.CallChatGptWhisper(config.AppConfig.CHATGPT, file)
	if err != nil {
		t.Fatalf("Error calling ChatGPT: %v", err)
	}

	log.Println(resp)
}

func TestCallAssistant(t *testing.T) {
	resp, err := gpt.CallAssistant("Ассистентом какого приложения ты являешься?")
	if err != nil {
		log.Fatalf("Произошла ошибка на стороне ИИ: %v", err)
	}
	log.Printf("Response from Assistant: %v", resp)
	log.Println("all ok!")
}
