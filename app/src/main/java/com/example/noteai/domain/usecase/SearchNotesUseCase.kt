package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchNotesUseCase(private val repository: NoteRepository) {
    suspend fun execute(query: String): Flow<List<Note>> {
        return repository.getAllNotes().map { notes ->
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.description.contains(query, ignoreCase = true)
            }
        }
    }
}