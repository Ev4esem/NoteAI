package com.example.noteai.presentation.favourite_screen

sealed interface FavouriteIntent {

    data class ChangeFavoriteStatus(val noteId: String) : FavouriteIntent

}