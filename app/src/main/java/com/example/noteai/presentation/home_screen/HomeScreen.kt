package com.example.noteai.presentation.home_screen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.noteai.data.repository.AudioRecordingService
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.ObserveEffect
import java.io.File


@Composable
fun MainScreen(
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permission = Manifest.permission.RECORD_AUDIO

    DisposableEffect(Unit) {
        onDispose {
            onIntent(HomeIntent.StopRecording)
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

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val outputFile = File(context.filesDir, "audio_recording_${System.currentTimeMillis()}.mp4")
        val isRecorded =
            uiState.audioState == AudioState.NOT_RECORDED || uiState.audioState == AudioState.INITIAL

        Button(
            onClick = {
                Log.d("MainScreen", "Click")
                if (isRecorded) {
                    if (uiState.audioPermissionState.isRecordingAllowing) {
                        ContextCompat.startForegroundService(
                            context,
                            AudioRecordingService.newIntent(context)
                        )
                        onIntent(HomeIntent.StartRecording(outputFile))
                    } else if (!uiState.audioPermissionState.isShowAudioPermissionDialog) {
                        onIntent(HomeIntent.AudioDialog.ShowAudioPermissionDialog)
                    } else {
                        Toast.makeText(context, "Разрешите доступ к микрофону", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    onIntent(HomeIntent.StopRecording)
                    onIntent(HomeIntent.SendRecordAudio)
                }
            }
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(10.dp),
                    color = Color.Red
                )
            } else {
                if (isRecorded) {
                    Text("Начать запись")
                } else {
                    Text("Остановить запись")
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(uiState.notes) { index, note ->
                Log.d("MainScreen", "Note item: $note")

                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Создано: ${note.createdAt}")
                    Text(text = note.title)
                }
            }
        }
    }
}
