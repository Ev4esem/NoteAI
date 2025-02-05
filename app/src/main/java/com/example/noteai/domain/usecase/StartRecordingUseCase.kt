package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.AudioRecordingRepository
import java.io.File

class StartRecordingUseCase(
    private val audioRecordingRepository: AudioRecordingRepository
) {
    operator fun invoke(outputFile: File) {
        audioRecordingRepository.startRecording(outputFile)
    }
}
