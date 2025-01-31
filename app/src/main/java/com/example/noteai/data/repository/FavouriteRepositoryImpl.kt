package com.example.noteai.data.repository

import com.example.noteai.data.local.db.FavouriteNotesDao
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteNotesDao: FavouriteNotesDao
) : FavouriteRepository {

    override val favouriteNotes: Flow<List<Note>>
        get() = TODO("Not yet implemented")

    override fun observeIsFavourite(noteId: Int): Flow<Boolean> {
        return favouriteNotesDao.observeIsFavourite(noteId)
    }

    override suspend fun addToFavourite(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromFavourite(noteId: Int) {
        favouriteNotesDao.removeFromFavourite(noteId)
    }
}