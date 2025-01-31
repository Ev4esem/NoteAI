package com.example.noteai.domain.usecase

import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.FavouriteRepository
import javax.inject.Inject

class AddToFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(note: Note) = favouriteRepository.addToFavourite(note)
}
