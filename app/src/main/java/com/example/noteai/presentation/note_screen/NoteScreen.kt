package com.example.noteai.presentation.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.noteai.presentation.home_screen.HomeViewModel

@Composable
fun NoteScreen(navController: NavHostController, viewModel: HomeViewModel, noteId: String?) {
    val uiState = viewModel.uiState.collectAsState()
    val note = uiState.value.notes.find { it.id.toString() == noteId }

    if (note == null) {
        Text(text = "Заметка не найдена")
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Название: ${note.title}")
            Text(text = "Описание: ${note.description}")
            Text(text = "Дата создания: ${note.createdAt}")
            Text(text = "Избранное: ${if (note.isFavorite) "Да" else "Нет"}")
        }
    }
}