package com.example.noteai.presentation.home_screen

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.noteai.data.repository.AudioRecordingService
import com.example.noteai.presentation.home_screen.HomeIntent.*
import com.example.noteai.presentation.navigation.NavRoute
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.ObserveEffect
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permission = Manifest.permission.RECORD_AUDIO

    DisposableEffect(Unit) {
        onDispose {
            onIntent(StopAndSendRecording)
        }
    }

    if (uiState.audioPermissionState.isShowAudioPermissionDialog && !uiState.audioPermissionState.isRecordingAllowing) {
        AudioRecorderPermissionScreen(
            permission = permission,
            onIntent = onIntent,
            uiState = uiState
        )
    }

    ObserveEffect(viewModel.effectFlow) { effect ->
        when (effect) {
            is HomeEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    val isRecorded =
        uiState.audioState == AudioState.NOT_RECORDED || uiState.audioState == AudioState.INITIAL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Main Screen") },
                actions = {
                    Button(
                        onClick = { navController.navigate(NavRoute.Favourite.route) },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Избраное", color = Color.White)
                    }
                },
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    if (isRecorded) {
                        if (uiState.audioPermissionState.isRecordingAllowing) {
                            val outputFile = File(context.filesDir, "audio_recording_${System.currentTimeMillis()}.mp4")
                            ContextCompat.startForegroundService(
                                context,
                                AudioRecordingService.newIntent(context)
                            )
                            onIntent(StartRecording(outputFile))
                        } else if (!uiState.audioPermissionState.isShowAudioPermissionDialog) {
                            onIntent(AudioDialog.ShowAudioPermissionDialog)
                        } else {
                            Toast.makeText(context, "Разрешите доступ к микрофону", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        onIntent(StopAndSendRecording)
                    }
                },
                modifier = Modifier.padding(16.dp),
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(10.dp),
                        color = Color.Red
                    )
                } else {
                    Text(if (isRecorded) "Начать запись" else "Остановить запись")
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notes) { note ->
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    when (it) {
                                        SwipeToDismissBoxValue.StartToEnd -> return@rememberSwipeToDismissBoxState true
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            onIntent(DeleteNote(note.id))
                                        }

                                        SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState true
                                    }
                                    return@rememberSwipeToDismissBoxState true
                                },
                                positionalThreshold = { it * .25f }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color by
                                    animateColorAsState(
                                        when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.Settled -> Color.LightGray
                                            SwipeToDismissBoxValue.StartToEnd -> Color.Green
                                            SwipeToDismissBoxValue.EndToStart -> Color.Red
                                        }, label = ""
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color)
                                    )
                                },
                                content = {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                navController.navigate(NavRoute.Note.route + "/${note.id}")
                                            },
                                        elevation = CardDefaults.cardElevation(8.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Создано: ${note.createdAt}",
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = note.title,
                                                style = MaterialTheme.typography.headlineMedium,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = note.description,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Button(
                                                onClick = {
                                                    onIntent(
                                                        ChangeFavoriteStatus(
                                                            note.id
                                                        )
                                                    )
                                                },
                                                modifier = Modifier.padding(top = 8.dp)
                                            ) {
                                                Text(if (note.isFavorite) "Удалить из избранного" else "Добавить в избранное")
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
