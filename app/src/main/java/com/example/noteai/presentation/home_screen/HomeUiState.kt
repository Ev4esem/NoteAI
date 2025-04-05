package com.example.noteai.presentation.home_screen

import com.example.noteai.R
import com.example.noteai.domain.entity.Note

data class HomeUiState(
    val notes: List<Note> = listOf(),
    val audioState: AudioState = AudioState.INITIAL,
    val audioPermissionState: AudioPermissionState = AudioPermissionState(),
    val loading: Boolean = false,
    val patternState: PatternState = PatternState.WORK,
    val isShowPatternsBottomSheet: Boolean = false,
    val searchQuery: String = ""
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

enum class PatternState(
    val titleRes: Int,
) {
    STUDY(titleRes = R.string.pattern_study_title),
    WORK(titleRes = R.string.pattern_work_title),
}