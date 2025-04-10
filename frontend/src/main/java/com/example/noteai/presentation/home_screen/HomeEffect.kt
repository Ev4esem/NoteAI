package com.example.noteai.presentation.home_screen

sealed interface HomeEffect {
    data class ShowToast(val message: String): HomeEffect
}