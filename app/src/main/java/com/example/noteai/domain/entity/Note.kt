package com.example.noteai.domain.entity

data class Note(
    val id: Int,
    val title: String,
    val audioFilePath: String,
    val transcription: String,
    val isFavorite: Boolean
)

