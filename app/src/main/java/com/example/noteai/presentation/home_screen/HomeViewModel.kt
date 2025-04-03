package com.example.noteai.presentation.home_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.usecase.AddNoteUseCase
import com.example.noteai.domain.usecase.ChangeFavouriteStatusUseCase
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.ObserveAmplitudeUseCase
import com.example.noteai.domain.usecase.SearchNotesUseCase
import com.example.noteai.domain.usecase.SendAudioUseCase
import com.example.noteai.domain.usecase.StartRecordingUseCase
import com.example.noteai.domain.usecase.StopRecordingUseCase
import com.example.noteai.utils.EffectHandler
import com.example.noteai.utils.IntentHandler
import com.example.noteai.utils.handlerError
import com.example.noteai.utils.launchSafe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(
    private val changeFavoriteStatusUseCase: ChangeFavouriteStatusUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val sendAudioUseCase: SendAudioUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val observeAmplitudeUseCase: ObserveAmplitudeUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
) : ViewModel(), IntentHandler<HomeIntent>, EffectHandler<HomeEffect> {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    override val effectChannel: Channel<HomeEffect> = Channel()
    private val _amplitudes = mutableStateListOf<Int>()
    val amplitudes: List<Int> get() = _amplitudes.toList()

    init {
        loadAllNotes()
    }

    private fun observeAmplitude() {
        launchSafe {
            observeAmplitudeUseCase().collect { amp ->
                _amplitudes.add(amp)
                if (_amplitudes.size > 100) {
                    _amplitudes.removeAt(0)
                }
            }
        }
    }

    override fun handlerIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.ChangeFavoriteStatus -> changeFavoriteStatus(intent.noteId)

            is HomeIntent.StartRecording -> startRecording(intent.file)

            is HomeIntent.StopAndSendRecording -> uploadAudio()

            is HomeIntent.AudioDialog.ShowAudioPermissionDialog -> showAudioPermissionDialog()

            is HomeIntent.AudioDialog.AudioPermissionGranted -> audioPermissionGranted()

            is HomeIntent.AudioDialog.DismissRationaleDialog -> dismissRationaleDialog()

            is HomeIntent.AudioDialog.ShowRationaleDialog -> showRationaleDialog()

            is HomeIntent.DeleteNote -> deleteNote(intent.noteId)

            is HomeIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)

            is HomeIntent.ChoosePattern -> choosePattern(intent.patternState)

            is HomeIntent.DismissPatternsBottomSheet -> dismissPatternsBottomSheet()

            is HomeIntent.ShowPatternsBottomSheet -> showPatternsBottomSheet()
        }
    }

    private fun showPatternsBottomSheet() {
        _uiState.update { currentState ->
            currentState.copy(
                isShowPatternsBottomSheet = true
            )
        }
    }

    private fun dismissPatternsBottomSheet() {
        _uiState.update { currentState ->
            currentState.copy(
                isShowPatternsBottomSheet = false
            )
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
        observeAmplitude()
    }

    private fun stopRecording() {
        stopRecordingUseCase()
        _uiState.update { currentState ->
            currentState.copy(
                audioState = AudioState.NOT_RECORDED
            )
        }
    }

    private fun uploadAudio() {
        stopRecording()
        launchSafe {
            sendAudioUseCase()
                .onEach {
                    _uiState.update { currentState ->
                        currentState.copy(loading = true)
                    }
                }
                .catch { exception ->
                    val errorMessage = handlerError(exception)
                    sendEffect(HomeEffect.ShowToast(errorMessage))
                }
                .collect { response ->
                    _uiState.update { currentState ->
                        currentState.copy(loading = false)
                    }
                    addNote(response)
                }
        }
    }

    private fun addNote(description: String) {
        launchSafe {
            val newNote = Note(
                id = System.currentTimeMillis(),
                title = "Заметка",
                description = description,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )

            addNoteUseCase(newNote)
        }
    }

    private fun deleteNote(noteId: Long) {
        launchSafe {
            deleteNoteUseCase(noteId)
        }
    }

    private fun changeFavoriteStatus(noteId: Long) {
        launchSafe {
            changeFavoriteStatusUseCase(noteId)
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
            )
        }
        if (query.isEmpty()) {
            loadAllNotes()
        } else {
            searchNotes(query)
        }
    }

    private fun choosePattern(patternState: PatternState) {
        _uiState.update { currentState ->
            currentState.copy(
                patternState = patternState,
            )
        }
        dismissPatternsBottomSheet()
    }

    private fun searchNotes(query: String) {
        launchSafe {
            searchNotesUseCase(query)
                .onEach { notes ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            loading = true,
                        )
                    }
                }
                .catch { exception ->
                    val errorMessage = handlerError(exception)
                    sendEffect(HomeEffect.ShowToast(errorMessage))
                }
                .collect { notes ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            loading = false,
                            notes = notes
                        )
                    }
                }
        }
    }

    private fun loadAllNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect { notes ->
                _uiState.update { currentState ->
                    currentState.copy(notes = notes)
                }
            }
        }
    }
}
