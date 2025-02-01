package com.example.noteai.data.repository

import NoteDao
import com.example.noteai.data.mapper.toDbModel
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteRepositoryImpl (
    private val noteDao: NoteDao
) : FavouriteRepository {

    override val favouriteNotes: Flow<List<Note>>
        get() = noteDao.getFavouriteNotes()
            .map { notesDb -> notesDb.map { it.toDomain() } }

    override fun observeIsFavourite(noteId: Int): Flow<Boolean> {
        return noteDao.observeIsFavourite(noteId)
    }

    override suspend fun addToFavourite(note: Note) {
        val updatedNote = note.copy(isFavorite = true)
        noteDao.addToFavourite(updatedNote.toDbModel())
    }

    override suspend fun removeFromFavourite(noteId: Int) {
        noteDao.removeFromFavourite(noteId)
    }
}



