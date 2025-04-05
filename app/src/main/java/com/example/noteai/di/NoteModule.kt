package com.example.noteai.di

import com.example.noteai.data.repository.FavouriteRepositoryImpl
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.data.service.AudioRecordingService
import com.example.noteai.domain.repository.FavouriteRepository
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.AddNoteUseCase
import com.example.noteai.domain.usecase.ChangeFavouriteStatusUseCase
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.GetFavouriteNotesUseCase
import com.example.noteai.domain.usecase.GetNoteByIdUseCase
import com.example.noteai.domain.usecase.ObserveAmplitudeUseCase
import com.example.noteai.domain.usecase.SearchNotesUseCase
import com.example.noteai.domain.usecase.SendAudioUseCase
import com.example.noteai.domain.usecase.StartRecordingUseCase
import com.example.noteai.domain.usecase.StopRecordingUseCase
import com.example.noteai.domain.usecase.UpdateNoteUseCase
import com.example.noteai.presentation.favourite_screen.FavouriteViewModel
import com.example.noteai.presentation.home_screen.HomeViewModel
import com.example.noteai.presentation.note_screen.NoteViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val noteModule = module {
    singleOf(::NoteRepositoryImpl) { bind<NoteRepository>() }
    singleOf(::FavouriteRepositoryImpl) { bind<FavouriteRepository>() }
    viewModelOf(::HomeViewModel)
    viewModelOf(::FavouriteViewModel)
    viewModelOf(::NoteViewModel)
    singleOf(::AudioRecordingService)
}

val useCaseModule = module {
    factoryOf(::SendAudioUseCase)
    factoryOf(::AddNoteUseCase)
    factoryOf(::ChangeFavouriteStatusUseCase)
    factoryOf(::DeleteNoteUseCase)
    factoryOf(::GetAllNotesUseCase)
    factoryOf(::GetFavouriteNotesUseCase)
    factoryOf(::GetNoteByIdUseCase)
    factoryOf(::UpdateNoteUseCase)
    factoryOf(::StartRecordingUseCase)
    factoryOf(::ObserveAmplitudeUseCase)
    factoryOf(::StopRecordingUseCase)
    factoryOf(::SearchNotesUseCase)
}