// storage/task.go
package storage

import (
	"fmt"
	"strings"
)

func AddTask(id string) error {
	query := `INSERT INTO tasks (id) VALUES (?)`
	_, err := DB.Exec(query, id)
	return err
}

func SetErrorState(id, content string) error {
	query := `UPDATE tasks SET state = 'error', content = ? WHERE id = ?`
	_, err := DB.Exec(query, content, id)
	return err
}

func SetReadyState(id, content string) error {
	query := `UPDATE tasks SET state = 'ready', content = ? WHERE id = ?`
	_, err := DB.Exec(query, content, id)
	return err
}

func GetTasksByIDs(ids []string) ([]Task, error) {
	if len(ids) == 0 {
		return []Task{}, nil
	}

	placeholders := strings.Repeat("?,", len(ids))
	placeholders = placeholders[:len(placeholders)-1] // remove trailing comma
	query := fmt.Sprintf("SELECT id, state, content FROM tasks WHERE id IN (%s)", placeholders)

	args := make([]interface{}, len(ids))
	for i, id := range ids {
		args[i] = id
	}

	rows, err := DB.Query(query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var tasks []Task
	for rows.Next() {
		task := Task{}
		err := rows.Scan(&task.ID, &task.State, &task.Content)
		if err != nil {
			return nil, err
		}
		tasks = append(tasks, task)
	}

	return tasks, nil
}

func GetAllTasks() ([]Task, error) {
	rows, err := DB.Query("SELECT id, state, content FROM tasks")
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var tasks []Task
	for rows.Next() {
		task := Task{}
		err := rows.Scan(&task.ID, &task.State, &task.Content)
		if err != nil {
			return nil, err
		}
		tasks = append(tasks, task)
	}

	return tasks, nil
}
