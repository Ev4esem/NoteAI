package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(): List<Note> = noteRepository.getAllNotes()
}
