package com.example.noteai.presentation.note_screen

import com.example.noteai.domain.entity.Note

sealed interface NoteIntent {

    data class UpdateNote(val updatedNote: Note) : NoteIntent

}