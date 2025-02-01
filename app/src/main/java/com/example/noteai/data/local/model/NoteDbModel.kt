package com.example.noteai.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavourite: Boolean = false
)
