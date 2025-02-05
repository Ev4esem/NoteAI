package com.example.noteai.data.mapper

import com.example.noteai.domain.entity.Note
import noteai.NoteDbEntity
fun NoteDbEntity.toDomain(): Note = Note(
        id = id,
        title = title,
        isFavorite = isFavourite.toBoolean() ,
        createdAt = createdAt
    )

fun Long.toBoolean(): Boolean = this == 1L

fun Note.toDbModel(): NoteDbEntity = NoteDbEntity(
        id = id,
        title = title,
        createdAt = System.currentTimeMillis(),
        isFavourite = isFavorite.toLong(),
    )

fun Boolean.toLong(): Long = if (this) 1 else 0