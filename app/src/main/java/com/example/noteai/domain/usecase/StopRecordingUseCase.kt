package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.AudioRecordingRepository

class StopRecordingUseCase(
    private val audioRecordingRepository: AudioRecordingRepository
) {
    operator fun invoke() {
        audioRecordingRepository.stopRecording()
    }
}
