package gpt

import (
	"io/ioutil"
	"log"
	"noteai/config"
	"os"
	"testing"
)

func init() {
	config.LoadLoading("../../.env")
}

func TestCallChatGPT(t *testing.T) {

	api_gpt := config.AppConfig.CHATGPT

	//cases := []struct {
	//	user      string
	//	assistant string
	//}{
	//	{
	//		user: "",
	//	},
	//}

	// Вызываем функцию CallChatGPT и получаем результат
	response, err := CallChatGPT(api_gpt,
		"Write a one-sentence bedtime story about a unicorn.")

	// Проверяем, что ошибка не возникла
	if err != nil {
		t.Fatalf("Error calling ChatGPT: %v", err)
	}

	// Проверяем, что ответ не пустой
	if response == "" {
		t.Error("Expected a non-empty response from ChatGPT")
	}

	// Логируем полученный ответ
	t.Logf("Response from ChatGPT: %s", response)
}

func TestReqIp(t *testing.T) {

	const urlIP string = "https://api.ipify.org"

	client := GenClientProxy()

	resp, err := client.Get(urlIP)
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()
	body, _ := ioutil.ReadAll(resp.Body)
	log.Println(string(body))
}

func TestCallChatGPTWhisper(t *testing.T) {
	// Открываем файл
	file, err := os.ReadFile("../../static/audio.mp3")
	if err != nil {
		log.Fatalf("Ошибка чтения файл: %v", err)
		return
	}

	resp, err := CallChatGptWhisper(config.AppConfig.CHATGPT, file)
	if err != nil {
		t.Fatalf("Error calling ChatGPT: %v", err)
	}

	log.Println(resp)
}
