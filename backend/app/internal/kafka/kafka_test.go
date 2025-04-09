package kafka

import (
	"noteai/config"
	"testing"
	"time"
)

func init() {
	config.LoadLoading("../.env")
}

func TestKafka(t *testing.T) {
	go Consumer()

	// Продюсер (ждёт, а потом пишет)
	time.Sleep(2 * time.Second)

	var kafkaMess Message
	kafkaMess.Value = []byte("hello world")
	Producer(kafkaMess)

	// Ждём, чтобы увидеть, что консьюмер прочитал
	time.Sleep(5 * time.Second)
}
