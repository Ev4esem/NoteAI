package com.example.noteai.presentation.home_screen

import com.example.noteai.domain.entity.Note

data class HomeUiState(
    val notes: List<Note> = listOf(),
    val audioState: AudioState = AudioState.INITIAL,
    val audioPermissionState: AudioPermissionState = AudioPermissionState(),
    val loading: Boolean = false,
) {
    data class AudioPermissionState(
        val isRecordingAllowing: Boolean = false,
        val isShowAudioPermissionDialog: Boolean = false,
        val isShowRationaleDialog: Boolean = false,
    )
}

enum class AudioState {
    RECORDED,
    NOT_RECORDED,
    INITIAL,
}
