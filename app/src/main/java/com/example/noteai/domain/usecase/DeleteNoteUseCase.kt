package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: Int) = noteRepository.deleteNote(noteId)
}
