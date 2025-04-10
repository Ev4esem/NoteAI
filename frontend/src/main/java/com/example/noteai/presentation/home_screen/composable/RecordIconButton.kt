package com.example.noteai.presentation.home_screen.composable

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.noteai.R
import com.example.noteai.data.service.AudioRecordingService
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeIntent.AudioDialog
import com.example.noteai.presentation.home_screen.HomeIntent.StartRecording
import com.example.noteai.presentation.home_screen.HomeIntent.StopAndSendRecording
import com.example.noteai.presentation.home_screen.HomeUiState
import java.io.File

@Composable
fun RecordIconButton(
    isRecorded: Boolean,
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    val context = LocalContext.current
    IconButton(
        modifier = Modifier
            .padding(
                start = 20.dp,
                top = 10.dp,
                end = 0.dp,
                bottom = 10.dp,
            )
            .size(56.dp),
        onClick = {
            when {
                !isRecorded && uiState.audioPermissionState.isRecordingAllowing -> {
                    val outputFile = File(
                        context.filesDir,
                        "audio_recording_${System.currentTimeMillis()}.mp4"
                    )
                    ContextCompat.startForegroundService(
                        context,
                        AudioRecordingService.newIntent(context)
                    )
                    onIntent(StartRecording(outputFile))
                }

                !isRecorded && !uiState.audioPermissionState.isShowAudioPermissionDialog -> {
                    onIntent(AudioDialog.ShowAudioPermissionDialog)
                }

                !isRecorded -> {
                    Toast.makeText(
                        context,
                        R.string.audio_permission_error_message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    onIntent(StopAndSendRecording)
                }
            }
        },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color(0xFF38452D),
            contentColor = Color.White,
        ),
    ) {
        Icon(
            painter = painterResource(id = if (isRecorded) R.drawable.vector_1 else R.drawable.microphone),
            contentDescription = stringResource(R.string.description_microphone_icon),
        )
    }
}