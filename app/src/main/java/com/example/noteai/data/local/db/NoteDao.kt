package com.example.noteai.data.local.db

import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteDao @Inject constructor (
    private val noteDataBase: NoteDataBase,
) {

    private val query get() = noteDataBase.noteDbEntityQueries

    fun getAllNotes(): Flow<List<Note>> = flow {
        val notes = query.getAllNotes().executeAsList().map {
            it.toDomain()
        }
        emit(notes)
    }

    suspend fun getNoteById(noteId: Long): Note? = withContext(Dispatchers.IO) {
        query.getNoteById(noteId).executeAsOneOrNull()?.toDomain()
    }

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        query.insertNote(note.toDbModel())
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        query.insertNote(note.toDbModel())
    }

    suspend fun deleteNote(noteId: Long) = withContext(Dispatchers.IO) {
        query.removeNote(noteId)
    }

}