package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository

class StopRecordingUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke() {
        noteRepository.stopRecording()
    }
}
