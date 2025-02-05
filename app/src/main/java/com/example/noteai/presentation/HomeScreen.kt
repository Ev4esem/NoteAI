package com.example.noteai.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.noteai.presentation.viewmodel.AudioViewModel


@Composable
fun MainScreen(viewModel: AudioViewModel) {
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                isRecording = !isRecording
                if (isRecording) viewModel.startRecording()
                else viewModel.stopRecording()
            }
        ) {
            Text(if (isRecording) "Остановить запись" else "Начать запись")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.uploadAudio() },
            enabled = !isRecording
        ) {
            Text("Отправить аудио")
        }
    }
}

