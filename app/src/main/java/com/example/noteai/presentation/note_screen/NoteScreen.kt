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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteai.domain.entity.Note

@Composable
fun NoteScreen(viewModel: NoteViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val note = uiState.note ?: throw IllegalArgumentException("Don't found note")

    val titleState = remember { mutableStateOf(note.title) }
    val descriptionState = remember {  mutableStateOf(note.description) }

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
                value = descriptionState.value,
                onValueChange = { descriptionState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    val updatedNote = Note(
                        id = note.id,
                        title = titleState.value,
                        description = descriptionState.value,
                        isFavorite = note.isFavorite,
                        createdAt = note.createdAt
                    )
                    viewModel.handlerIntent(
                        NoteIntent.UpdateNote(
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