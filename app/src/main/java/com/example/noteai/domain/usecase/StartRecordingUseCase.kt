package com.example.noteai.domain.usecase

import com.example.noteai.domain.repository.NoteRepository
import java.io.File

class StartRecordingUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(outputFile: File) {
        noteRepository.startRecording(outputFile)
    }
}
