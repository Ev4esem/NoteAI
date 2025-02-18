package com.example.noteai.data.mapper

import com.example.noteai.domain.entity.Note
import noteai.NoteDbEntity

fun NoteDbEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    description = description,
    isFavorite = isFavourite.toBoolean(),
    createdAt = createdAt
)

fun Note.toDbModel(): NoteDbEntity = NoteDbEntity(
    id = id,
    title = title,
    description = description,
    createdAt = System.currentTimeMillis(),
    isFavourite = isFavorite.toLong()
)

fun Long.toBoolean(): Boolean = this == 1L

fun Boolean.toLong(): Long = if (this) 1 else 0
