package com.example.noteai.presentation.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.noteai.domain.entity.Note
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeViewModel

@Composable
fun NoteScreen(navController: NavHostController, viewModel: HomeViewModel, noteId: String?) {
    val uiState = viewModel.uiState.collectAsState()
    val note = uiState.value.notes.find { it.id.toString() == noteId }

    val titleState = remember { mutableStateOf(note!!.title) }
    val descriptionState = remember { note?.let { mutableStateOf(it.description) } }

    if (note == null) {
        Text(text = "Заметка не найдена")
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Название:")
            TextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Text(text = "Описание:")
            TextField(
                value = descriptionState?.value ?: "",
                onValueChange = { descriptionState?.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    val updatedNote = Note(
                        id = note.id, // сохраняем оригинальный id
                        title = titleState.value,
                        description = descriptionState?.value ?: "",
                        isFavorite = note.isFavorite,
                        createdAt = note.createdAt
                    )
                    viewModel.handlerIntent(
                        HomeIntent.UpdateNote(
                            noteId = note.id,
                            updatedNote = updatedNote
                        )
                    )
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Сохранить изменения")
            }
            Text(text = "Дата создания: ${note.createdAt}")
            Text(text = "Избранное: ${if (note.isFavorite) "Да" else "Нет"}")
        }
    }
}