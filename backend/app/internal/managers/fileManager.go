package managers

import (
	"errors"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
)

type FileManager interface {
	WriteFile(filename string, data []byte) error
	ReadFile(filename string) ([]byte, error)
	Open(filename string) (*os.File, error)
	Delete() error
}

type File struct {
	Path string `json:"path"`
}

func (f *File) Read() ([]byte, error) {
	data, err := os.ReadFile(f.Path)
	if err != nil {
		return nil, fmt.Errorf("не удалось прочитать файл %s", err)
	}
	return data, nil
}

func (f *File) Open() (*os.File, error) {
	file, err := os.Open(f.Path)
	if err != nil {
		log.Fatalf("Не удалось открыть файл %s", err)
		return nil, err
	}
	return file, nil
}

const uploadDir = "./uploads"

func (f *File) Write(file io.Reader) (error, int) {

	// Создаем директорию, если она не существует
	if _, err := os.Stat(uploadDir); os.IsNotExist(err) {
		err := os.MkdirAll(uploadDir, os.ModePerm)
		if err != nil {
			return errors.New("не удалось создать директорию для загрузки"), http.StatusInternalServerError
		}
	}

	// Формируем путь к файлу для сохранения
	filePath := filepath.Join(uploadDir, f.Path)

	// Открываем файл для записи
	outFile, err := os.Create(filePath)
	if err != nil {
		return errors.New("не удалось сохранить файл"), http.StatusInternalServerError
	}

	// Копируем содержимое загруженного файла в новый файл
	_, err = io.Copy(outFile, file)
	if err != nil {
		return errors.New("ошибка при записи файла"), http.StatusInternalServerError
	}

	errClosedFile := outFile.Close()
	if errClosedFile != nil {
		return errors.New("не удалось закрыть файл"), http.StatusInternalServerError
	}

	return nil, 0
}

func (f *File) Delete() {
	filePath := filepath.Join(uploadDir, f.Path)
	err := os.Remove(filePath)
	if err != nil {
		log.Printf("файл по каким то причинам не удалился %s", err)
	}
}
