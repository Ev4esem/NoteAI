package gpt

import (
	"log"
	"net/http"
	"net/url"
	"noteai/config"
)

func GenClientProxy() *http.Client {

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
