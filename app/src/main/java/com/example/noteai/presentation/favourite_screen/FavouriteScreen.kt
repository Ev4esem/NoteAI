package com.example.noteai.presentation.favourite_screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FavouriteScreen(viewModel: FavouriteViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn {
        items(uiState.favouriteNotes) { note ->
            Text(text = "Note Title: ${note.title}")
            Text(text = "Note Description: ${note.description}")
        }
    }
}