package com.example.noteai.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.repository.AudioRepositoryImpl
import com.example.noteai.data.service.AudioRecordingService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File

class AudioViewModel(
    application: Application,
    private val audioRecordingService: AudioRecordingService,
    private val audioRepository: AudioRepositoryImpl
) : AndroidViewModel(application) {

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    fun startRecording() {
        val context: Context = getApplication()
        val outputFile = File(context.filesDir, "audio_recording_${System.currentTimeMillis()}.mp4")
        audioRecordingService.startRecording(outputFile)
    }

    fun stopRecording() {
        audioRecordingService.stopRecording()
    }

    fun uploadAudio() {
        viewModelScope.launch {
            val file = audioRepository.getPendingAudio()
            if (file == null) {
                _toastMessage.emit("Нет записей для отправки")
                return@launch
            }

            // Попытка отправить аудио
            val result = audioRepository.uploadAudio(file)
            if (result.isSuccess) {
                file.delete() // Удаляем файл после успешной отправки
                _toastMessage.emit("Аудио отправлено")
            } else {
                val exception = result.exceptionOrNull()
                _toastMessage.emit("Ошибка: ${exception?.message ?: "Неизвестная ошибка"}")
            }
        }
    }
}
