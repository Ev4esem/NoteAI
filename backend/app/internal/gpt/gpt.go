package gpt

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"mime/multipart"
	"net/http"
)

type RequestText struct {
	Model       string  `json:"model"`
	Temperature float64 `json:"temperature"`
	TopP        float64 `json:"top_p"`
	Input       string  `json:"input"`
}

type OpenAIResponse struct {
	ID          string        `json:"id"`
	Object      string        `json:"object"`
	Status      string        `json:"status"`
	Model       string        `json:"model"`
	Output      []OutputBlock `json:"output"`
	Temperature float64       `json:"temperature"`
	TopP        float64       `json:"top_p"`
	Usage       Usage         `json:"usage"`
}

type OutputBlock struct {
	Type    string    `json:"type"` // "message"
	ID      string    `json:"id"`
	Status  string    `json:"status"` // "completed"
	Role    string    `json:"role"`   // "assistant"
	Content []Content `json:"content"`
}

type Content struct {
	Type        string   `json:"type"` // "output_text"
	Text        string   `json:"text"`
	Annotations []string `json:"annotations"`
}

type Usage struct {
	InputTokens  int `json:"input_tokens"`
	OutputTokens int `json:"output_tokens"`
	TotalTokens  int `json:"total_tokens"`
}

func CallChatGPT(apiKey string, contentUser string) (string, error) {
	// Формируем запрос
	requestData := RequestText{
		Model:       "gpt-4o-mini",
		Input:       contentUser,
		Temperature: 1,
		TopP:        1,
	}

	// Преобразуем запрос в JSON
	requestBody, err := json.Marshal(requestData)
	if err != nil {
		log.Fatalf("Failed to marshal request: %v", err)
	}

	// Отправляем запрос в OpenAI
	url := "https://api.openai.com/v1/responses"

	client := GenClientProxy()
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(requestBody))
	if err != nil {
		log.Fatalf("Failed to create request: %v", err)
	}

	// Устанавливаем заголовки
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+apiKey)

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

func CallChatGptWhisper(apiKey string, fileData []byte) (string, error) {
	// Буфер и multipart writer
	var body bytes.Buffer
	writer := multipart.NewWriter(&body)

	// Создаем поле "file" и копируем в него данные
	part, err := writer.CreateFormFile("file", "audio_file.mp3")
	if err != nil {
		return "", fmt.Errorf("не удалось создать поле файла: %v", err)
	}
	_, err = io.Copy(part, bytes.NewReader(fileData))
	if err != nil {
		return "", fmt.Errorf("не удалось скопировать содержимое файла: %v", err)
	}

	// Поле "model"
	if err := writer.WriteField("model", "whisper-1"); err != nil {
		return "", fmt.Errorf("не удалось добавить поле model: %v", err)
	}

	// Поле "language"
	if err := writer.WriteField("language", "ru"); err != nil {
		return "", fmt.Errorf("не удалось добавить поле language: %v", err)
	}

	// Завершаем запись multipart/form-data
	if err := writer.Close(); err != nil {
		return "", fmt.Errorf("не удалось закрыть writer: %v", err)
	}

	// Создаём POST-запрос
	req, err := http.NewRequest("POST", "https://api.openai.com/v1/audio/transcriptions", &body)
	if err != nil {
		return "", fmt.Errorf("не удалось создать запрос: %v", err)
	}

	// Заголовки
	req.Header.Set("Authorization", "Bearer "+apiKey)
	req.Header.Set("Content-Type", writer.FormDataContentType())

	// HTTP-клиент
	client := GenClientProxy()

	// Выполнение запроса
	resp, err := client.Do(req)
	if err != nil {
		return "", fmt.Errorf("ошибка выполнения запроса: %v", err)
	}
	defer resp.Body.Close()

	// Чтение ответа
	responseBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("ошибка чтения ответа: %v", err)
	}

	// Проверка HTTP-статуса
	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("статус %d: %s", resp.StatusCode, string(responseBody))
	}

	// Парсинг JSON-ответа в мапу
	var result map[string]interface{}
	if err := json.Unmarshal(responseBody, &result); err != nil {
		return "", fmt.Errorf("не удалось распарсить JSON: %v", err)
	}

	// Например, достаём поле "text"
	text, ok := result["text"].(string)
	if !ok {
		return "", fmt.Errorf("поле 'text' не найдено или не строка")
	}

	return text, nil
}
