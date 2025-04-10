package com.example.noteai.presentation.favourite_screen

import com.example.noteai.domain.entity.Note

data class FavouriteUiState(
    val favouriteNotes: List<Note> = listOf(),
    val loading: Boolean = false,
)
