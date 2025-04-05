package com.example.noteai.presentation.note_screen

sealed interface NoteIntent {

    data object ChangeEditMode: NoteIntent

    data class ChangeTitle(val title: String): NoteIntent

    data class ChangeDescription(val description: String): NoteIntent

}