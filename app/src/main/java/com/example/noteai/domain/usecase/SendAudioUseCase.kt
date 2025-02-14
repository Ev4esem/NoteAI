package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

class SendAudioUseCase(
    private val noteRepository: NoteRepository,
) {
    operator fun invoke(): Flow<Response> = noteRepository.uploadAudio()
}