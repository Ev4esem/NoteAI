package gpt

import (
	"log"
	"net/http"
	"net/url"
	"noteai/config"
)

type headers struct {
	Authorization string `header:"Authorization"`
}

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

func genClientProxy() *http.Client {

	proxyURL, err := url.Parse(config.AppConfig.PROXY_URL)
	if err != nil {
		log.Fatalf("Ошибка разбора прокси URL: %v", err)
	}

	// Создаем транспорт с указанием прокси
	transport := &http.Transport{
		Proxy: http.ProxyURL(proxyURL),
	}

	// Создаем HTTP-клиент с указанным транспортом
	client := &http.Client{
		Transport: transport,
	}

	return client
}

func setOpenAIHeaders(r *http.Request) {
	r.Header.Set("Content-Type", "application/json")
	r.Header.Set("Authorization", "Bearer "+config.AppConfig.CHATGPT)
}

func setOpenAIHeadersAssistants(r *http.Request) {
	setOpenAIHeaders(r)
	r.Header.Set("OpenAI-Beta", "assistants=v2")
}
