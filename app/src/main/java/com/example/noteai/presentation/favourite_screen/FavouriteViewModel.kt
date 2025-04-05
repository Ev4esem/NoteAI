package com.example.noteai.presentation.favourite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.usecase.ChangeFavouriteStatusUseCase
import com.example.noteai.domain.usecase.GetFavouriteNotesUseCase
import com.example.noteai.utils.IntentHandler
import com.example.noteai.utils.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val changeFavoriteStatusUseCase: ChangeFavouriteStatusUseCase,
    private val getFavouriteNotesUseCase: GetFavouriteNotesUseCase,
) : ViewModel(), IntentHandler<FavouriteIntent> {

    private val _uiState = MutableStateFlow(FavouriteUiState())
    val uiState: StateFlow<FavouriteUiState> = _uiState.asStateFlow()

    override fun handlerIntent(intent: FavouriteIntent) {
        when (intent) {
            is FavouriteIntent.ChangeFavoriteStatus -> changeFavoriteStatus(intent.noteId)
        }
    }

    init {
        viewModelScope.launch {
            getFavouriteNotes()
        }
    }

    private fun changeFavoriteStatus(noteId: Long) {
        launchSafe {
            changeFavoriteStatusUseCase(noteId)
        }
    }


    private fun getFavouriteNotes() {
        viewModelScope.launch {
            getFavouriteNotesUseCase()
                .onEach {
                    _uiState.update { currentState ->
                        currentState.copy(
                            loading = true
                        )
                    }
                }
                .collect { favouriteNotes ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            favouriteNotes = favouriteNotes,
                            loading = false,
                        )
                    }
                }
        }
    }
}