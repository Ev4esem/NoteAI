package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.FavouriteRepository

class AddToFavouriteUseCase (
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(noteId: Long) = favouriteRepository.changeFavouriteStatus(noteId)
}
