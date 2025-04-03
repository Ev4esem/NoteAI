package com.example.noteai.presentation.permission_dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.noteai.R

@Composable
fun AudioRecorderPermissionsDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text((stringResource(R.string.audio_permission_dialog_title))) },
        text = { Text(stringResource(R.string.audio_permission_dialog_text)) },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(stringResource(R.string.audio_permission_dialog_confirm_button))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.audio_permission_dialog_dismiss_button))
            }
        }
    )
}