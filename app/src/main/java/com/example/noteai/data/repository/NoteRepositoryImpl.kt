package com.example.noteai.data.repository

import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteRepositoryImpl : NoteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()

    override suspend fun getAllNotes(): Flow<List<Note>> {
        val notes = noteDao.getAllNotes().map { notes ->
            notes.map { note ->
                note.toDomain()
            }
        }
        return notes
    }

    override suspend fun getNoteById(noteId: Long): Note? {
       return noteDao.getNoteById(noteId)
    }

    override suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNote(noteId)
    }
}


