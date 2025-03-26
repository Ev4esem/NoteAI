package com.example.noteai.data.local.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import noteai.NoteDbEntity

class NoteDao(
    private val noteDataBase: NoteDataBase,
) {

    private val query get() = noteDataBase.noteDbEntityQueries

    fun getAllNotes(): Flow<List<NoteDbEntity>> = query
        .getAllNotes()
        .asFlow()
        .mapToList(Dispatchers.IO)

    fun getAllFavouriteNotes(): Flow<List<NoteDbEntity>> = query
        .getAllFavouriteNotes()
        .asFlow()
        .mapToList(Dispatchers.IO)

    fun searchNotes(query: String): Flow<List<NoteDbEntity>> = this.query
        .searchNotes(query)
        .asFlow()
        .mapToList(Dispatchers.IO)


    suspend fun changeFavouriteStatus(noteId: Long) = withContext(Dispatchers.IO) {
        query.changeFavouriteStatus(noteId)
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