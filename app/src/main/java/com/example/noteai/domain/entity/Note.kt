package com.example.noteai.domain.entity

data class Note(
    val id: Int,
    val title: String,
    val transcription: String,
    val isFavorite: Boolean,
    val createdAt: Long
)


