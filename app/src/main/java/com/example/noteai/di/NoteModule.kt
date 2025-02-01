package com.example.noteai.di

import com.example.noteai.data.repository.FavouriteRepositoryImpl
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.repository.FavouriteRepository
import com.example.noteai.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NoteModule {

    @NoteScope
    @Binds
    fun bindFavouriteRepositoryImplToFavouriteRepository(
        favouriteRepositoryImpl: FavouriteRepositoryImpl
    ) : FavouriteRepository

    @NoteScope
    @Binds
    fun bindNoteRepositoryImplToNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ) : NoteRepository

}