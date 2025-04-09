// storage/types.go
package storage

import "database/sql"

type Task struct {
	ID      string
	State   string
	Content sql.NullString
}
