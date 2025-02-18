package com.example.noteai.presentation.favourite_screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.noteai.presentation.home_screen.HomeViewModel

@Composable
fun FavouriteScreen(navController: NavController, viewModel: HomeViewModel) {

    val uiState = viewModel.uiState.collectAsState()
    val favoriteNotes = uiState.value.notes.filter { it.isFavorite }

    LazyColumn {
        itemsIndexed(favoriteNotes) { index, note ->
            Text(text = "Note Title: ${note.title}")
            Text(text = "Note Description: ${note.description}")
        }
    }
}