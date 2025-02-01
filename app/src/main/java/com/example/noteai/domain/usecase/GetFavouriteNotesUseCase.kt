package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouriteNotesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke() = favouriteRepository.favouriteNotes
}
