package com.example.noteai.presentation.note_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.usecase.GetNoteByIdUseCase
import com.example.noteai.domain.usecase.UpdateNoteUseCase
import com.example.noteai.utils.IntentHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
): ViewModel(), IntentHandler<NoteIntent> {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    fun getNoteById(noteId: Long?) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase(getNoteId(noteId))
            _uiState.update { currentState ->
                currentState.copy(
                    note = note,
                )
            }
        }
    }

    fun clear() {
        _uiState.update { currentState ->
            currentState.copy(
                note = null,
            )
        }
    }

    private fun getNoteId(noteId: Long?): Long = noteId ?: throw IllegalArgumentException("noteId equals null")

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