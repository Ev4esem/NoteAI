package gpt

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"noteai/config"
	"time"
)

type CreateThreadResponse struct {
	ThreadID string `json:"id"`
}

type createRunResponse struct {
	Id string `json:"Id"`
}

type isCompletedResponse struct {
	Status string `json:"Status"`
}

type contentMessage struct {
	Data []struct {
		Content []struct {
			Type string `json:"type"`
			Text struct {
				Value       string        `json:"value"`
				Annotations []interface{} `json:"annotations"`
			} `json:"text"`
		} `json:"content"`
	} `json:"data"`
}

func CallAssistant(content string) (*string, error) {
	resp, err := createThread(content)
	if err != nil {
		return nil, err
	}
	threadId := resp.ThreadID

	runResp, err := runAssistant(threadId)
	if err != nil {
		return nil, err
	}
	runId := runResp.Id

	for {
		isCompletedResp, err := isCompletedTask(threadId, runId)
		if err != nil {
			return nil, err
		}
		if isCompletedResp.Status == "completed" {
			break
		}
		time.Sleep(1 * time.Second)
	}

	result, err := completedMessage(threadId)
	if err != nil {
		return nil, err
	}
	if len(result.Data) > 0 && len(result.Data[0].Content) > 0 {
		fmt.Println("Первый ответ ассистента:", result.Data[0].Content[0].Text.Value)
		return &result.Data[0].Content[0].Text.Value, nil
	}
	return nil, errors.New("Нет данных в content")
}

func createThread(content string) (*CreateThreadResponse, error) {
	url := "https://api.openai.com/v1/threads"

	body := map[string]interface{}{
		"messages": []map[string]string{
			{
				"role":    "user",
				"content": content,
			},
		},
	}

	bodyBytes, err := json.Marshal(body)
	if err != nil {
		return nil, err
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(bodyBytes))
	if err != nil {
		return nil, err
	}

	setOpenAIHeadersAssistants(req)

	client := genClientProxy()
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	respBody, err := io.ReadAll(resp.Body)

	println(string(respBody))

	var result CreateThreadResponse
	err = json.Unmarshal(respBody, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

func runAssistant(threadId string) (*createRunResponse, error) {
	url := "https://api.openai.com/v1/threads/" + threadId + "/runs"

	body := map[string]interface{}{
		"assistant_id": config.AppConfig.ASSISTAND_ID,
	}

	bodyBytes, err := json.Marshal(body)
	if err != nil {
		return nil, err
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(bodyBytes))

	setOpenAIHeadersAssistants(req)

	client := genClientProxy()
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	respBody, err := io.ReadAll(resp.Body)

	var result createRunResponse
	err = json.Unmarshal(respBody, &result)
	if err != nil {
		return nil, err
	}
	return &result, nil

}

func isCompletedTask(threadId string, runId string) (*isCompletedResponse, error) {
	url := "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}
	setOpenAIHeadersAssistants(req)

	client := genClientProxy()
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)

	var result isCompletedResponse
	err = json.Unmarshal(respBody, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil
}

func completedMessage(threadId string) (*contentMessage, error) {
	url := "https://api.openai.com/v1/threads/" + threadId + "/messages"

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}
	setOpenAIHeadersAssistants(req)

	client := genClientProxy()
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)

	var result contentMessage
	err = json.Unmarshal(respBody, &result)
	if err != nil {
		return nil, err
	}

	return &result, nil

}
