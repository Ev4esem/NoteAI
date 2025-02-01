package com.example.noteai.data.mapper

import com.example.noteai.data.local.model.NoteDbModel
import com.example.noteai.domain.entity.Note

fun NoteDbModel.toDomain(): Note {
    return Note(
        id = this.id,
        title = this.text,
        transcription = this.text,
        isFavorite = this.isFavourite,
        createdAt = this.createdAt
    )
}

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(
        id = this.id,
        text = this.transcription,
        isFavourite = this.isFavorite
    )
}
