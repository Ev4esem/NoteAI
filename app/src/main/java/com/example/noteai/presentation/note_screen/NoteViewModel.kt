package com.example.noteai.presentation.note_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.usecase.GetNoteByIdUseCase
import com.example.noteai.domain.usecase.UpdateNoteUseCase
import com.example.noteai.utils.Constants
import com.example.noteai.utils.IntentHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
): ViewModel(), IntentHandler<NoteIntent> {

    private val noteId: Long = checkNotNull(savedStateHandle[Constants.NOTE_ID])

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getNoteById()
        }
    }

    private suspend fun getNoteById() {
        val note = getNoteByIdUseCase(noteId)
        _uiState.update { currentState ->
            currentState.copy(
                note = note,
            )
        }
    }

    override fun handlerIntent(intent: NoteIntent) {
        viewModelScope.launch {
            when(intent) {
                is NoteIntent.UpdateNote -> updateNote(intent.updatedNote)
            }
        }
    }

    private suspend fun updateNote(note: Note) {
        updateNoteUseCase(note)
    }

}