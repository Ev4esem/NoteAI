package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentNotesStatusUseCase(
    private val repository: NoteRepository
) {
   suspend operator fun invoke(): Flow<List<Note>> {
      return repository.getCurrentNotesStatus()
   }
}