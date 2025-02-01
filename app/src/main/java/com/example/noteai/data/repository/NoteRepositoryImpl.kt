package com.example.noteai.data.repository

import NoteDao
import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.di.NoteScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@NoteScope
class NoteRepositoryImpl (
    private val notesDao: NoteDao,
) : NoteRepository {

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { notesDbModelList ->
            notesDbModelList.map { it.toDomain() }
        }
    }

    override suspend fun getNote(noteId: Int): Note? {
        val noteDbModel = notesDao.getNoteById(noteId)
        return noteDbModel?.toDomain()
    }

    override suspend fun addNote(note: Note) {
        val noteDbModel = note.toDbModel()
        notesDao.addToFavourite(noteDbModel)
    }

    override suspend fun updateNote(note: Note) {
        val noteDbModel = note.toDbModel()
        notesDao.updateNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }
}


