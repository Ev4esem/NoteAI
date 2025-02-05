package com.example.noteai.domain.repository

import java.io.File

interface AudioRepository {
    suspend fun uploadAudio(file: File): Result<Unit>
    fun getPendingAudio(): File?
}
