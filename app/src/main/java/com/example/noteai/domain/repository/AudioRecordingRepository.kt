package com.example.noteai.domain.repository

import java.io.File

interface AudioRecordingRepository {
    fun startRecording(outputFile: File)
    fun stopRecording()
}
