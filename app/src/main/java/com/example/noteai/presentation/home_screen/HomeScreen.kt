package com.example.noteai.presentation.home_screen

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.noteai.data.repository.AudioRecordingService
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import java.io.File


@Composable
fun MainScreen(
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permission = Manifest.permission.RECORD_AUDIO

    if (uiState.audioPermissionState.isShowAudioPermissionDialog && !uiState.audioPermissionState.isRecordingAllowing) {
        AudioRecorderPermissionScreen(
            permission = permission,
            onIntent = onIntent,
            uiState = uiState
        )
    }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val outputFile = File(context.filesDir, "audio_recording_${System.currentTimeMillis()}.mp4")
        Button(
            onClick = {
                if(uiState.audioPermissionState.isRecordingAllowing) {
                    ContextCompat.startForegroundService(
                        context,
                        AudioRecordingService.newIntent(context)
                    )
                    onIntent(HomeIntent.StartRecording(outputFile))
                } else if(!uiState.audioPermissionState.isShowAudioPermissionDialog) {
                    onIntent(HomeIntent.AudioDialog.ShowAudioPermissionDialog)
                } else {
                    Toast.makeText(context, "Разрешите доступ к микрофону", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text("Начать запись")
        }

        Button(
            onClick = {
                onIntent(HomeIntent.StopRecording)
            }
        ) {
            Text("Остановить запись")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onIntent(HomeIntent.SendRecordAudio)
            },
        ) {
            Text("Отправить аудио")
        }
    }
}

