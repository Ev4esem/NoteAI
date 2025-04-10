package com.example.noteai.domain.entity


data class Note(
    val id: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val status: StatusNote = StatusNote.WAIT,
)
