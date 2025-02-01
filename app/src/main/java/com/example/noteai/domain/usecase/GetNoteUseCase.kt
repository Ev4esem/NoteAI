package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int): Note? = noteRepository.getNoteById(noteId)
}
