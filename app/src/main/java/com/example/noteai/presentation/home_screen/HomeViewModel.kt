package com.example.noteai.presentation.home_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.usecase.AddNoteUseCase
import com.example.noteai.domain.usecase.ChangeFavouriteStatusUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.SendAudioUseCase
import com.example.noteai.domain.usecase.StartRecordingUseCase
import com.example.noteai.domain.usecase.StopRecordingUseCase
import com.example.noteai.utils.EffectHandler
import com.example.noteai.utils.IntentHandler
import com.example.noteai.utils.handlerError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(
    private val changeFavoriteStatusUseCase: ChangeFavouriteStatusUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val sendAudioUseCase: SendAudioUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
) : ViewModel(), IntentHandler<HomeIntent>, EffectHandler<HomeEffect> {

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
            when (intent) {
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

    /*private fun uploadAudio() {
        viewModelScope.launch {
            sendAudioUseCase()
                .onEach {
                    _uiState.update { currentState ->
                        currentState.copy(loading = true)
                    }
                    delay(3000)
                }
                .catch {
                    val message = handlerError(it)
                    sendEffect(HomeEffect.ShowToast(message))
                }
                .collect {
                    addNote()
                }
        }
    }*/

    private fun uploadAudio() {
        viewModelScope.launch {
            sendAudioUseCase()
                .onStart {
                    _uiState.update { it.copy(loading = true) }
                }
                .onEach { response ->
                    Log.d("HomeViewModel", response)
                    addNote(response)
                    delay(3000)
                }
                .catch { exception ->
                    val errorMessage = handlerError(exception)
                    sendEffect(HomeEffect.ShowToast(errorMessage))
                }
                .onCompletion {
                    _uiState.update { it.copy(loading = false) }
                }
                .collect {

                }
        }
    }

    private suspend fun addNote(description: String) {
        val newNote = Note(
            id = System.currentTimeMillis(),
            title = "Teкст",
            description = description,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )

        addNoteUseCase(newNote)
    }

    private suspend fun init() {
        getAllNotesUseCase().collect { notes ->
            _uiState.update { currentState ->
                currentState.copy(notes = notes)
            }
        }
    }
}
