package com.example.noteai.di

import com.example.noteai.data.repository.FavouriteRepositoryImpl
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.repository.FavouriteRepository
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.AddNoteUseCase
import com.example.noteai.domain.usecase.AddToFavouriteUseCase
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.GetFavouriteNotesUseCase
import com.example.noteai.domain.usecase.GetNoteByIdUseCase
import com.example.noteai.domain.usecase.UpdateNoteUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val noteModule = module {
    singleOf(::NoteRepositoryImpl) { bind<NoteRepository>() }
    singleOf(::FavouriteRepositoryImpl) { bind<FavouriteRepository>() }
    factoryOf(::AddNoteUseCase)
    factoryOf(::AddToFavouriteUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::GetAllNotesUseCase)
    factoryOf(::GetFavouriteNotesUseCase)
    factoryOf(::GetNoteByIdUseCase)
    factoryOf(::UpdateNoteUseCase)
}