package kafka

import (
	"github.com/segmentio/kafka-go"
	"log"
	"noteai/config"
)

var url string

const topic = "audio"
const group = "noteai"

func InitTopic() {
	url = config.AppConfig.KAFKA_HOST + ":9092"
	conn, err := kafka.Dial("tcp", url)
	if err != nil {
		log.Fatal(err)
	}
	defer conn.Close()

	// создание топика
	err = conn.CreateTopics(kafka.TopicConfig{
		Topic:             topic,
		NumPartitions:     1,
		ReplicationFactor: 1,
	})
	if err != nil {
		log.Fatal("Ошибка создания топика:", err)
	}
}

type Message struct {
	kafka.Message
}
