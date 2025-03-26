package com.example.noteai.presentation.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.noteai.R
import com.example.noteai.utils.Constants
import com.example.noteai.utils.Constants.NOTE_SCREEN_BACK
import kotlinx.coroutines.delay

@Composable
fun NoteScreen(noteId: Long?, viewModel: NoteViewModel, navController: NavHostController) {

    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val note = uiState.note

    if (note != null) {

        var titleState by remember { mutableStateOf(note.title) }
        var descriptionState by remember { mutableStateOf(note.description) }
        var isEditing by remember { mutableStateOf(false) }

        LaunchedEffect(titleState, descriptionState) {
            if (isEditing) {
                delay(500)
                viewModel.handlerIntent(
                    NoteIntent.UpdateNote(
                        updatedNote = note.copy(
                            title = titleState,
                            description = descriptionState
                        )
                    )
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
                .paint(painterResource(R.drawable.background))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = NOTE_SCREEN_BACK,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = {
                    isEditing = !isEditing
                }) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                        contentDescription = Constants.NOTE_SCREEN_EDIT,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = titleState,
                    onValueChange = { if (isEditing) titleState = it },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = isEditing,
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        disabledLabelColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                TextField(
                    value = descriptionState,
                    onValueChange = { if (isEditing) descriptionState = it },
                    textStyle = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = isEditing,
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        disabledLabelColor = Color.Transparent,
                        disabledTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}
