package com.example.noteai.presentation.permission_dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioRecorderPermissionScreen(
    permissionState: PermissionState,
    uiState: HomeUiState,
    onIntent: (HomeIntent.AudioDialog) -> Unit,
) {
    LaunchedEffect(permissionState.status) {
        when {
            permissionState.status.isGranted -> {
                onIntent(HomeIntent.AudioDialog.AudioPermissionGranted)
            }
            permissionState.status.shouldShowRationale -> {
                onIntent(HomeIntent.AudioDialog.ShowRationaleDialog)
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }

    if (uiState.audioPermissionState.isShowRationaleDialog) {
        AudioRecorderPermissionsDialog(
            onDismiss = {
                onIntent(HomeIntent.AudioDialog.DismissRationaleDialog)
            },
            onRequestPermission = {
                permissionState.launchPermissionRequest()
                onIntent(HomeIntent.AudioDialog.DismissRationaleDialog)
            }
        )
    }
}
