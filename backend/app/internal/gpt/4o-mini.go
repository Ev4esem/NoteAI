package gpt

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
)

func CallChatGPT(contentUser string) (string, error) {
	// Формируем запрос
	requestData := RequestText{
		Model:       "gpt-4o",
		Input:       contentUser,
		Temperature: 0.3,
		TopP:        1,
	}

	// Преобразуем запрос в JSON
	requestBody, err := json.Marshal(requestData)
	if err != nil {
		log.Fatalf("Failed to marshal request: %v", err)
	}

	// Отправляем запрос в OpenAI
	url := "https://api.openai.com/v1/responses"

	client := genClientProxy()
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(requestBody))
	if err != nil {
		log.Fatalf("Failed to create request: %v", err)
	}

	setOpenAIHeaders(req)

	// Отправляем запрос
	resp, err := client.Do(req)
	if err != nil {
		log.Fatalf("Failed to send request: %v", err)
	}
	defer resp.Body.Close()

	// Читаем ответ
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Fatalf("Failed to read response: %v", err)
	}

	// Выводим необработанный ответ в формате JSON
	fmt.Println("Raw Response Body:")
	fmt.Println(string(body))

	var response OpenAIResponse
	if err := json.Unmarshal(body, &response); err != nil {
		return "", fmt.Errorf("не удалось распарсить ответ: %v", err)
	}

	if len(response.Output) == 0 || len(response.Output[0].Content) == 0 {
		return "", fmt.Errorf("ответ пустой")
	}

	text := response.Output[0].Content[0].Text
	return text, nil

}
