package com.example.noteai.presentation.home_screen

import com.example.noteai.domain.entity.Note
import java.io.File

sealed interface HomeIntent {

    data class StartRecording(val file: File) : HomeIntent

    data object StopRecording : HomeIntent

    data class ChangeFavoriteStatus(val noteId: Long) : HomeIntent

    data object SendRecordAudio : HomeIntent

    data class DeleteNote(val noteId: Long) : HomeIntent

    data class UpdateNote(val noteId: Long, val updatedNote: Note) : HomeIntent

    sealed interface AudioDialog : HomeIntent {

        data object AudioPermissionGranted : AudioDialog

        data object ShowAudioPermissionDialog : AudioDialog

        data object ShowRationaleDialog : AudioDialog

        data object DismissRationaleDialog : AudioDialog

    }
}