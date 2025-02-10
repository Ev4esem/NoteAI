package com.example.noteai.presentation.home_screen

// TODO Использовать когда будет сделано https://github.com/Ev4esem/NoteAI/issues/5
sealed interface HomeEffect {
    data class ShowToast(val message: String): HomeEffect
}