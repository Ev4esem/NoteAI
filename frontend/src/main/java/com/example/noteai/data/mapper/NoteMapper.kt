package com.example.noteai.data.mapper

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.entity.StatusNote
import com.example.noteai.domain.entity.StatusNote.*
import noteai.NoteDbEntity

fun NoteDbEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    status = status.toStatusNote(),
    description = description,
    isFavorite = isFavourite.toBoolean(),
    createdAt = createdAt
)

fun Note.toDbModel(): NoteDbEntity = NoteDbEntity(
    id = id,
    title = title,
    description = description,
    createdAt = System.currentTimeMillis(),
    isFavourite = isFavorite.toLong(),
    status = status.toStatusNote()
)

fun String.toStatusNote(): StatusNote = when (this) {
    "ready" -> READY
    "error" -> ERROR
    else -> WAIT
}

fun StatusNote.toStatusNote(): String = when (this) {
    WAIT -> "wait"
    READY -> "ready"
    ERROR -> "error"
}

fun Long.toBoolean(): Boolean = this == 1L

fun Boolean.toLong(): Long = if (this) 1 else 0
