package storage

import (
	"database/sql"
	_ "github.com/mattn/go-sqlite3"
	"log"
)

var DB *sql.DB

func InitDB(path string) error {
	var err error
	DB, err = sql.Open("sqlite3", path)
	if err != nil {
		return err
	}

	createTable := `CREATE TABLE IF NOT EXISTS tasks (
		id TEXT PRIMARY KEY,
		state TEXT NOT NULL DEFAULT 'wait',
		content TEXT
	);`

	_, err = DB.Exec(createTable)
	if err != nil {
		return err
	}

	log.Println("📦 База данных и таблица tasks инициализированы")
	return nil
}
