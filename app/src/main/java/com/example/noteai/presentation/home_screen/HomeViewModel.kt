package com.example.noteai.presentation.home_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.repository.NoteRepositoryImpl
import com.example.noteai.domain.usecase.ChangeFavouriteStatusUseCase
import com.example.noteai.domain.usecase.GetPendingAudioUseCase
import com.example.noteai.domain.usecase.SendAudioUseCase
import com.example.noteai.domain.usecase.StartRecordingUseCase
import com.example.noteai.domain.usecase.StopRecordingUseCase
import com.example.noteai.utils.EffectHandler
import com.example.noteai.utils.IntentHandler
import com.example.noteai.utils.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(
    private val noteRepositoryImpl: NoteRepositoryImpl,
    private val changeFavoriteStatusUseCase: ChangeFavouriteStatusUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val getPendingAudioUseCase: GetPendingAudioUseCase,
    private val sendAudioUseCase: SendAudioUseCase,
) : ViewModel(), IntentHandler<HomeIntent>, EffectHandler<HomeEffect> {

    private val TAG = this::class.java.simpleName

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    override val effectChannel: Channel<HomeEffect> = Channel()

    init {
        viewModelScope.launch {
            init()
        }
    }

    override fun handlerIntent(intent: HomeIntent) {
        viewModelScope.launch {
            when(intent) {
                is HomeIntent.ChangeFavoriteStatus -> changeFavoriteStatusUseCase(intent.noteId)

                is HomeIntent.SendRecordAudio -> uploadAudio()

                is HomeIntent.StartRecording -> startRecording(intent.file)

                is HomeIntent.StopRecording -> stopRecording()

                is HomeIntent.AudioDialog.ShowAudioPermissionDialog -> showAudioPermissionDialog()

                is HomeIntent.AudioDialog.AudioPermissionGranted -> audioPermissionGranted()

                is HomeIntent.AudioDialog.DismissRationaleDialog -> dismissRationaleDialog()

                is HomeIntent.AudioDialog.ShowRationaleDialog -> showRationaleDialog()

            }
        }
    }

    private fun showRationaleDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                audioPermissionState = currentState.audioPermissionState.copy(
                    isShowRationaleDialog = true,
                ),
            )
        }
    }

    private fun dismissRationaleDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                audioPermissionState = currentState.audioPermissionState.copy(
                    isShowRationaleDialog = false,
                ),
            )
        }
    }

    private fun audioPermissionGranted() {
        _uiState.update { currentState ->
            currentState.copy(
                audioPermissionState = currentState.audioPermissionState.copy(
                    isRecordingAllowing = true,
                    isShowAudioPermissionDialog = false,
                ),
            )
        }
    }

    private fun showAudioPermissionDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                audioPermissionState = currentState.audioPermissionState.copy(
                    isShowAudioPermissionDialog = true,
                ),
            )
        }
    }

    private fun startRecording(outputFile: File) {
        startRecordingUseCase(outputFile)
        _uiState.update { currentState ->
            currentState.copy(
                audioState = AudioState.RECORDED
            )
        }
    }

    private fun stopRecording() {
        stopRecordingUseCase()
        _uiState.update { currentState ->
            currentState.copy(
                audioState = AudioState.NOT_RECORDED
            )
        }
    }

    // TODO добавить метод когда будет сделан бэкенд https://github.com/Ev4esem/NoteAI/issues/5
    private fun uploadAudio() {
        viewModelScope.launch {
            Log.d(TAG, noteRepositoryImpl.getCurrentAudioFile()?.name.toString())
            sendAudioUseCase()
//            sendAudioUseCase().collect {
//                when(it) {
//                    is Response.Error -> {
//                        it.message?.let { message ->
//                            sendEffect(
//                                HomeEffect.ShowToast(message)
//                            )
//                        }
//                    }
//                    is Response.Loading -> {
//                        _uiState.update { currentState ->
//                            currentState.copy(
//                                loading = true,
//                            )
//                        }
//                    }
//                    is Response.Success<*> -> {
//                        _uiState.update { currentState ->
//                            currentState.copy(
//                                loading = false,
//                                audioState = AudioState.INITIAL
//                            )
//                        }
//                    }
//                }
//            }
        }
    }

    private suspend fun init() {
        val file = getPendingAudioUseCase()
        _uiState.update { currentState ->
            currentState.copy(
                audioState = if (file == null) AudioState.INITIAL else AudioState.NOT_RECORDED,
            )
        }
    }
}