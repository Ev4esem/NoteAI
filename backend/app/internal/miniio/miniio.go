package miniio

import (
	"bytes"
	"context"
	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"io"
	"log"
	"mime/multipart"
	"noteai/config"
)

var minioClient *minio.Client

func StartMinIO() {
	client, err := minio.New(config.AppConfig.MINIIO_HOST+":9000", &minio.Options{
		Creds:  credentials.NewStaticV4("minioadmin", "minioadmin123", ""),
		Secure: false,
	})
	if err != nil {
		log.Fatalln("Ошибка подключения к MinIO:", err)
	}
	minioClient = client

	// Создаём bucket, если его нет
	exists, err := client.BucketExists(context.Background(), "audio")
	if err != nil {
		log.Fatalln("Ошибка проверки bucket:", err)
	}
	if !exists {
		err = client.MakeBucket(context.Background(), "audio", minio.MakeBucketOptions{})
		if err != nil {
			log.Fatalln("Ошибка создания bucket:", err)
		}
	}
}

func UploadToMinIO(file multipart.File, header *multipart.FileHeader, objectName string) (minio.UploadInfo, error) {
	return minioClient.PutObject(
		context.Background(),
		"audio",
		objectName,
		file,
		header.Size,
		minio.PutObjectOptions{ContentType: "audio/mpeg"}, // можно динамически тоже подставить
	)
}

func DownloadFromMinIO(objectName string) ([]byte, error) {
	object, err := minioClient.GetObject(context.Background(), "audio", objectName, minio.GetObjectOptions{})
	if err != nil {
		return nil, err
	}
	defer object.Close()

	var buf bytes.Buffer
	_, err = io.Copy(&buf, object)
	if err != nil {
		return nil, err
	}

	return buf.Bytes(), nil
}

func DeleteAudio(objectName string) {
	minioClient.RemoveObject(context.Background(), "audio", objectName, minio.RemoveObjectOptions{})
}
