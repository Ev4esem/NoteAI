package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.FavouriteRepository
import javax.inject.Inject

class RemoveFromFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(noteId: Int) = favouriteRepository.removeFromFavourite(noteId)
}
