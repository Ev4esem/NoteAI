package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {

    val favouriteNotes: Flow<List<Note>>

    fun observeIsFavourite(noteId: Int): Flow<Boolean>

    suspend fun addToFavourite(note: Note)

    suspend fun removeFromFavourite(noteId: Int)
}