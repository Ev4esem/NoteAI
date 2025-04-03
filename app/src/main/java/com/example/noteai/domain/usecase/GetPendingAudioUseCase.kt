package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository

class GetPendingAudioUseCase(
    private val noteRepository: NoteRepository
) {

    operator fun invoke() = noteRepository.getPendingAudio()

}
