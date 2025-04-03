package com.example.noteai.presentation.home_screen

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.noteai.R
import com.example.noteai.data.service.AudioRecordingService
import com.example.noteai.domain.entity.Note
import com.example.noteai.presentation.components.AudioVisualizer
import com.example.noteai.presentation.components.NoteCard
import com.example.noteai.presentation.components.TitleTopBar
import com.example.noteai.presentation.components.model.AmplitudeType
import com.example.noteai.presentation.components.model.WaveformAlignment
import com.example.noteai.presentation.home_screen.HomeIntent.AudioDialog
import com.example.noteai.presentation.home_screen.HomeIntent.ChangeFavoriteStatus
import com.example.noteai.presentation.home_screen.HomeIntent.ChoosePattern
import com.example.noteai.presentation.home_screen.HomeIntent.DeleteNote
import com.example.noteai.presentation.home_screen.HomeIntent.DismissPatternsBottomSheet
import com.example.noteai.presentation.home_screen.HomeIntent.ShowPatternsBottomSheet
import com.example.noteai.presentation.home_screen.HomeIntent.StartRecording
import com.example.noteai.presentation.home_screen.HomeIntent.StopAndSendRecording
import com.example.noteai.presentation.home_screen.HomeIntent.UpdateSearchQuery
import com.example.noteai.presentation.navigation.NavRoute
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.ObserveEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
    paddingValues: PaddingValues
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TitleTopBar(stringResource(R.string.title_main_screen))
        SearchNote(
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

@Composable
fun SearchNote(
    onSearch: (String) -> Unit,
    searchQuery: String
) {
    TextField(
        value = searchQuery,
        onValueChange = { onSearch(it) },
        placeholder = {
            Text(
                text = stringResource(R.string.title_search_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        },
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = stringResource(R.string.description_search_icon),
                tint = Color.White
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearch("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.description_clear_icon),
                        tint = Color.White
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(52.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF747D6C),
            unfocusedContainerColor = Color(0xFF747D6C),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        )
    )
}

@Composable
fun MainFloatingButton(
    uiState: HomeUiState,
    amplitudes: List<Int>,
    onIntent: (HomeIntent) -> Unit,
) {
    val context = LocalContext.current
    val isRecorded =
        uiState.audioState == AudioState.NOT_RECORDED || uiState.audioState == AudioState.INITIAL
    var isRecording by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF1C2317),
                shape = RoundedCornerShape(50.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 26.dp)
                    .size(56.dp),
                color = Color(0xFF1C2317),
                strokeWidth = 4.dp,
                trackColor = Color.White
            )
        } else {
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
                        isRecorded && uiState.audioPermissionState.isRecordingAllowing -> {
                            val outputFile = File(
                                context.filesDir,
                                "audio_recording_${System.currentTimeMillis()}.mp4"
                            )
                            isRecording = true
                            ContextCompat.startForegroundService(
                                context,
                                AudioRecordingService.newIntent(context)
                            )
                            onIntent(StartRecording(outputFile))
                        }

                        isRecorded && !uiState.audioPermissionState.isShowAudioPermissionDialog -> {
                            onIntent(AudioDialog.ShowAudioPermissionDialog)
                        }

                        isRecorded -> {
                            Toast.makeText(
                                context,
                                R.string.audio_permission_error_message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            onIntent(StopAndSendRecording)
                            isRecording = false
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF38452D),
                    contentColor = Color.White,
                ),
            ) {
                Icon(
                    painter = painterResource(id = if (isRecorded) R.drawable.microphone else R.drawable.vector_1),
                    contentDescription = stringResource(R.string.description_microphone_icon),
                )

            }
        }
        if (isRecording && !uiState.loading) {
            AudioVisualizer(
                amplitudes = amplitudes,
                waveformAlignment = WaveformAlignment.Center,
                amplitudeType = AmplitudeType.Max,
                spikeAnimationSpec = tween(80),
                waveformBrush = SolidColor(Color.White),
            )
        } else if (!uiState.loading) {
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
        if (uiState.isShowPatternsBottomSheet) {
            PatternsBottomSheet(
                currentPatternState = uiState.patternState,
                onIntent = onIntent,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternsBottomSheet(
    currentPatternState: PatternState,
    onIntent: (HomeIntent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            onIntent(DismissPatternsBottomSheet)
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        containerColor = Color(0xFF38452D),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 68.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.title_template),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PatternState.entries.forEach { pattern ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2C3520)
                        ),
                        border = if (currentPatternState == pattern) BorderStroke(
                            2.dp,
                            Color.White
                        ) else null,
                        onClick = {
                            onIntent(ChoosePattern(pattern))
                        },
                        content = {
                            Box(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF122505))
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = stringResource(pattern.titleRes),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFFFFFFFF),
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    navController: NavController,
    onIntent: (HomeIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 28.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(items = notes, key = { it.id }) { note ->
            NoteItem(
                note = note,
                navController = navController,
                onIntentRemove = onIntent,
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    navController: NavController,
    onIntentRemove: (HomeIntent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    delay(1.seconds)
                    onIntentRemove(DeleteNote(note.id))
                }
                return@rememberSwipeToDismissBoxState true
            } else {
                return@rememberSwipeToDismissBoxState false
            }
        },
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when (swipeToDismissBoxState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.Transparent
                }, label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .padding(end = 10.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.description_delete_icon),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        content = {
            NoteCard(
                title = note.title,
                onFavoriteClick = {
                    onIntentRemove(ChangeFavoriteStatus(note.id))
                },
                onClick = {
                    navController.navigate(NavRoute.Note.route + "/${note.id}")
                },
                isFavorite = note.isFavorite,
                createdAt = note.createdAt
            )
        }
    )
}
