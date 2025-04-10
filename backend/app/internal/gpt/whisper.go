package gpt

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
)

func CallChatGptWhisper(apiKey string, fileName string, fileData []byte) (string, error) {
	// Буфер и multipart writer
	var body bytes.Buffer
	writer := multipart.NewWriter(&body)

	// Создаем поле "file" и копируем в него данные
	part, err := writer.CreateFormFile("file", fileName)
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
	client := genClientProxy()

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
