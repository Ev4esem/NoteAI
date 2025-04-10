package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository

class GetNoteByIdUseCase (
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String): Note? = noteRepository.getNoteById(noteId)
}
