package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository

class AddNoteUseCase (
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(note: Note) = noteRepository.addNote(note)
}
