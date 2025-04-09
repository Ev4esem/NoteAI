package kafka

import (
	"context"
	"github.com/segmentio/kafka-go"
	"log"
)

func Producer(kafkaMess Message) {
	ctx := context.Background()

	writer := kafka.NewWriter(kafka.WriterConfig{
		Brokers: []string{url},
		Topic:   topic,
	})
	defer writer.Close()

	log.Println("📤 Отправка сообщения...")

	err := writer.WriteMessages(ctx, kafkaMess.Message)
	if err != nil {
		log.Fatal("Ошибка записи:", err)
	}

}
