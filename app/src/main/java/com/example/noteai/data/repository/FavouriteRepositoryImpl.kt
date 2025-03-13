package com.example.noteai.data.repository

import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FavouriteRepositoryImpl : FavouriteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()

    override val favouriteNotes: Flow<List<Note>>
        get() = noteDao.getAllFavouriteNotes()
            .map { notes -> notes.map { it.toDomain() } }

    override suspend fun changeFavouriteStatus(noteId: Long) {
        noteDao.changeFavouriteStatus(noteId)
    }
}
