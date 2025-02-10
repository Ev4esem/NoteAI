package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.utils.Response
import kotlinx.coroutines.flow.Flow

class SendAudioUseCase(
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke() = noteRepository.uploadAudio()
}