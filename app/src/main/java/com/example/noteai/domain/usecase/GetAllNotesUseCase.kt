package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(): Flow<List<Note>> = noteRepository.getAllNotes()
}
