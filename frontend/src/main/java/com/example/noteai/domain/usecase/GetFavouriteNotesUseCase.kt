package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.FavouriteRepository

class GetFavouriteNotesUseCase (
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke() = favouriteRepository.favouriteNotes
}
