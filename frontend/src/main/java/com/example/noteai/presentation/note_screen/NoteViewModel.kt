package com.example.noteai.presentation.note_screen

import androidx.lifecycle.ViewModel
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.usecase.GetNoteByIdUseCase
import com.example.noteai.domain.usecase.UpdateNoteUseCase
import com.example.noteai.utils.IntentHandler
import com.example.noteai.utils.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class NoteViewModel(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
) : ViewModel(), IntentHandler<NoteIntent> {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    fun getNoteById(noteId: String?) {
        launchSafe {
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

    private fun getNoteId(noteId: String?): String =
        noteId ?: throw IllegalArgumentException("noteId equals null")

    override fun handlerIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.ChangeEditMode -> changeEditMode()
            is NoteIntent.ChangeDescription -> changeDescription(intent.description)
            is NoteIntent.ChangeTitle -> changeTitle(intent.title)
        }
    }

    private fun changeDescription(description: String) {
        _uiState.update { currentState ->
            currentState.copy(
                note = currentState.note?.copy(
                    description = description
                )
            )
        }
    }

    private fun changeTitle(title: String) {
        _uiState.update { currentState ->
            currentState.copy(
                note = currentState.note?.copy(
                    title = title
                )
            )
        }
    }

    private fun changeEditMode() {
        _uiState.update { currentState ->
            if (currentState.isEditing) {
                updateNote(
                    note = currentState.note
                )
            }
            currentState.copy(
                isEditing = !currentState.isEditing
            )
        }
    }

    private fun updateNote(note: Note?) {
        val noteNotNull = note ?: throw IllegalArgumentException("note == null")
        launchSafe {
            updateNoteUseCase(noteNotNull)
        }
    }
}