package com.example.noteai.data.repository

import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.di.NoteScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@NoteScope
class NoteRepositoryImpl @Inject constructor (
    private val notesDao: NoteDao,
) : NoteRepository {

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes()
    }

    override suspend fun getNoteById(noteId: Long): Note? {
       return notesDao.getNoteById(noteId)
    }

    override suspend fun addNote(note: Note) {
        notesDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        notesDao.updateNote(note)
    }

    override suspend fun deleteNote(noteId: Long) {
        notesDao.deleteNote(noteId)
    }
}


