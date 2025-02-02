package com.example.noteai.domain.entity

data class Note(
    val id: Long,
    val title: String,
    val isFavorite: Boolean,
    val createdAt: Long
)


