package com.example.noteai.presentation.home_screen

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noteai.R
import com.example.noteai.presentation.components.AudioVisualizer
import com.example.noteai.presentation.components.BottomNavigationBar
import com.example.noteai.presentation.components.TitleTopBar
import com.example.noteai.presentation.components.model.AmplitudeType
import com.example.noteai.presentation.components.model.WaveformAlignment
import com.example.noteai.presentation.home_screen.HomeIntent.ShowPatternsBottomSheet
import com.example.noteai.presentation.home_screen.HomeIntent.StopAndSendRecording
import com.example.noteai.presentation.home_screen.HomeIntent.UpdateSearchQuery
import com.example.noteai.presentation.home_screen.composable.NoteList
import com.example.noteai.presentation.home_screen.composable.PatternsBottomSheet
import com.example.noteai.presentation.home_screen.composable.RecordIconButton
import com.example.noteai.presentation.home_screen.composable.SearchField
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.ObserveEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val isRecorded =
        uiState.audioState == AudioState.RECORDED

    LaunchedEffect(Unit) {
        if(permissionState.status.isGranted) {
            onIntent(HomeIntent.AudioDialog.AudioPermissionGranted)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecorded) {
                onIntent(StopAndSendRecording)
            }
        }
    }

    if (uiState.audioPermissionState.isShowAudioPermissionDialog && !uiState.audioPermissionState.isRecordingAllowing) {
        AudioRecorderPermissionScreen(
            permissionState = permissionState,
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

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            TitleTopBar(
                title = stringResource(R.string.title_main_screen)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
        },
        floatingActionButton = {
            MainFloatingButton(
                uiState = uiState,
                isRecorded = isRecorded,
                onIntent = onIntent,
                amplitudes = viewModel.amplitudes,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SearchField(
                onSearch = { query ->
                    viewModel.handlerIntent(UpdateSearchQuery(query))
                },
                searchQuery = uiState.searchQuery,
            )
            NoteList(
                notes = uiState.notes,
                navController = navController,
                onIntent = onIntent,
            )
        }

    }
}


@Composable
fun MainFloatingButton(
    uiState: HomeUiState,
    amplitudes: List<Int>,
    isRecorded: Boolean,
    onIntent: (HomeIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF1C2317),
                shape = RoundedCornerShape(50.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecordIconButton(
            isRecorded = isRecorded,
            uiState = uiState,
            onIntent = onIntent,
        )
        when {
            uiState.loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 26.dp)
                        .size(56.dp),
                    color = Color(0xFF1C2317),
                    strokeWidth = 4.dp,
                    trackColor = Color.White
                )
            }

            isRecorded && !uiState.loading -> {
                AudioVisualizer(
                    amplitudes = amplitudes,
                    waveformAlignment = WaveformAlignment.Center,
                    amplitudeType = AmplitudeType.Max,
                    spikeAnimationSpec = tween(80),
                    waveformBrush = SolidColor(Color.White),
                )
            }

            !uiState.loading -> {
                IconButton(
                    modifier = Modifier
                        .padding(
                            start = 0.dp,
                            top = 10.dp,
                            end = 20.dp,
                            bottom = 10.dp,
                        )
                        .size(56.dp),
                    onClick = {
                        onIntent(ShowPatternsBottomSheet)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF38452D),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline),
                        contentDescription = stringResource(R.string.description_outline_icon),
                    )
                }
            }
        }
        if (uiState.isShowPatternsBottomSheet) {
            PatternsBottomSheet(
                currentPatternState = uiState.patternState,
                onIntent = onIntent,
            )
        }
    }
}
