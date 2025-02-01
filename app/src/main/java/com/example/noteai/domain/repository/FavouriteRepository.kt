package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {

    val favouriteNotes: Flow<List<Note>>

    suspend fun changeFavouriteStatus(noteId: Long)

}