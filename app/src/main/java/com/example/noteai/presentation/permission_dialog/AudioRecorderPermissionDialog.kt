package com.example.noteai.presentation.permission_dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.noteai.utils.Constants

@Composable
fun AudioRecorderPermissionsDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text((Constants.AUDIO_PERMISSION_DIALOG_TITLE)) },
        text = { Text(Constants.AUDIO_PERMISSION_DIALOG_TEXT) },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(Constants.AUDIO_PERMISSION_DIALOG_CONFIRM_BUTTON)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(Constants.AUDIO_PERMISSION_DIALOG_DISMISS_BUTTON)
            }
        }
    )
}