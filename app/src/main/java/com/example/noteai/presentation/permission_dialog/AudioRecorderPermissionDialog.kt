package com.example.noteai.presentation.permission_dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AudioRecorderPermissionsDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Разрешение на запись аудио") },
        text = { Text("Для записи аудио необходимо предоставить разрешение на использование микрофона.") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Запросить разрешение")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}