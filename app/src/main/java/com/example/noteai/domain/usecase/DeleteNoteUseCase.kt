package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository

class DeleteNoteUseCase (
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: Long) = noteRepository.deleteNote(noteId)
}
