package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.SharedFlow

class ObserveAmplitudeUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): SharedFlow<Int> = repository.observeAmplitude()
}