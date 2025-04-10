package kafka

import (
	"context"
	"github.com/segmentio/kafka-go"
	"log"
	"noteai/config"
	"noteai/internal/gpt"
	"noteai/internal/miniio"
	"noteai/storage"
)

func Consumer() {
	ctx := context.Background()
	miniio.StartMinIO()

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

		audioID := string(msg.Key)     // UUID
		extension := string(msg.Value) // .mp3 /

		objectName := audioID + extension

		log.Printf("📥 Обработка ID: %s", audioID)

		audioData, err := miniio.DownloadFromMinIO(objectName)
		if err != nil {
			log.Println("❌ Ошибка загрузки из MinIO:", err)
			storage.SetErrorState(audioID, "Не удалось получить аудио из хранилища")
			continue
		}

		// 1 этап обработка аудио
		txtWhisper, errWhisp := gpt.CallChatGptWhisper(config.AppConfig.CHATGPT, objectName, audioData)
		if txtWhisper == "" || errWhisp != nil {
			log.Println("❌ Whisper ошибка:", errWhisp)
			storage.SetErrorState(audioID, "Ошибка обработки Whisper: "+errWhisp.Error())
			continue
		}

		// 2 этап обработка текста
		respGPT, errGPT := gpt.CallAssistant(txtWhisper)
		if respGPT == nil || *respGPT == "" || errGPT != nil {
			log.Println("❌ Assistant ошибка:", errGPT)
			storage.SetErrorState(audioID, "Ошибка обработки ChatGPT: "+errGPT.Error())
			continue
		}

		errWriteReadyState := storage.SetReadyState(audioID, *respGPT)
		if errWriteReadyState != nil {
			log.Println("❌ Ошибка записи результата:", err)
			continue
		}

		if err := reader.CommitMessages(ctx, msg); err != nil {
			log.Println("⚠️ Ошибка коммита:", err)
			continue
		}
		miniio.DeleteAudio(objectName)

		log.Printf("✅ Готово: %s", audioID)
	}
}
