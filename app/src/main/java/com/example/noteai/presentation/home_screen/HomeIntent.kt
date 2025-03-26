package com.example.noteai.presentation.home_screen

import java.io.File

sealed interface HomeIntent {

    data class StartRecording(val file: File) : HomeIntent

    data object StopAndSendRecording : HomeIntent

    data class ChangeFavoriteStatus(val noteId: Long) : HomeIntent

    data class DeleteNote(val noteId: Long) : HomeIntent

    data class UpdateSearchQuery(val query: String) : HomeIntent

    sealed interface AudioDialog : HomeIntent {

        data object AudioPermissionGranted : AudioDialog

        data object ShowAudioPermissionDialog : AudioDialog

        data object ShowRationaleDialog : AudioDialog

        data object DismissRationaleDialog : AudioDialog

    }
}