package com.example.noteai.domain.usecase

import com.example.noteai.data.model.AudioResponse
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class SendAudioUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(): Flow<AudioResponse> = noteRepository.uploadAudio()
}