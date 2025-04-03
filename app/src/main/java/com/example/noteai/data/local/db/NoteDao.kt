package com.example.noteai.data.local.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    fun searchNotes(query: String): Flow<List<NoteDbEntity>> =
        this.query.searchNotes("%$query%")
            .asFlow()
            .mapToList(Dispatchers.IO)

    fun changeFavouriteStatus(noteId: Long) = query.changeFavouriteStatus(noteId)

    fun getNoteById(noteId: Long): Note? =
        query.getNoteById(noteId).executeAsOneOrNull()?.toDomain()

    fun addNote(note: Note) = query.insertNote(note.toDbModel())

    fun updateNote(note: Note) = query.insertNote(note.toDbModel())

    fun deleteNote(noteId: Long) = query.removeNote(noteId)

}