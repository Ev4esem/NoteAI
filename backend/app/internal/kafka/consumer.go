package kafka

import (
	"context"
	"github.com/segmentio/kafka-go"
	"log"
	"noteai/config"
	"noteai/internal/gpt"
	"noteai/storage"
)

func Consumer() {
	ctx := context.Background()
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{url},
		Topic:   topic,
		GroupID: group,
	})
	defer reader.Close()

	for {
		msg, err := reader.ReadMessage(ctx)
		if err != nil {
			log.Println("Ошибка чтения:", err)
			continue
		}

		kafkaKey := string(msg.Key)

		log.Printf("📥 Получено: \n %s: %s", kafkaKey)

		// 1 этап обработка аудио
		txtWhisper, errWhisp := gpt.CallChatGptWhisper(config.AppConfig.CHATGPT, msg.Value)
		if txtWhisper == "" {
			err := storage.SetErrorState(kafkaKey, "Не получилось обработать аудио файл"+errWhisp.Error())
			if err != nil {
				log.Println("Не удалось сохранить изменения в бд" + err.Error())
				continue
			}
		}

		// 2 этап обработка текста
		respGPT, errWhisp := gpt.CallChatGPT(config.AppConfig.CHATGPT, txtWhisper)
		if respGPT == "" {
			err := storage.SetErrorState(kafkaKey, "ИИ не ответил попробуйте еще"+errWhisp.Error())
			if err != nil {
				log.Println("Не удалось сохранить изменения в бд" + err.Error())
				continue
			}
		}

		errWriteReadyState := storage.SetReadyState(kafkaKey, respGPT)
		if errWriteReadyState != nil {
			log.Println("Не удалось сохранить изменения в бд" + err.Error())
			continue
		}

		errCommit := reader.CommitMessages(ctx, msg)
		if errCommit != nil {
			log.Println("Ошибка при коммите:", errCommit)
			continue
		}
		log.Println("Сообщение закомичено и задача выполнена успешно!")
	}
}
