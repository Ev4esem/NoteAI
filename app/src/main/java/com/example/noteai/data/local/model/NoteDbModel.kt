package com.example.noteai.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_notes")
data class NoteDbModel(
    @PrimaryKey val id: Int
)
