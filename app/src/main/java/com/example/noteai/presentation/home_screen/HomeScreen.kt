package com.example.noteai.presentation.home_screen

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.noteai.R
import com.example.noteai.data.repository.AudioRecordingService
import com.example.noteai.domain.entity.Note
import com.example.noteai.presentation.components.NoteCard
import com.example.noteai.presentation.components.TitleTopBar
import com.example.noteai.presentation.home_screen.HomeIntent.*
import com.example.noteai.presentation.navigation.NavRoute
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.Constants
import com.example.noteai.utils.Constants.DESCRIPTION_CLEAR_ICON
import com.example.noteai.utils.Constants.DESCRIPTION_MICROPHONE_ICON
import com.example.noteai.utils.Constants.DESCRIPTION_OUTLINE_ICON
import com.example.noteai.utils.Constants.TITLE_SEARCH_PLACEHOLDER
import com.example.noteai.utils.Constants.TITLE_TEMPLATE
import com.example.noteai.utils.Constants.TITLE_TEMPLATE_STUDY
import com.example.noteai.utils.ObserveEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
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
        TitleTopBar(Constants.TITLE_MAIN_SCREEN)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchNote(
    onSearch: (String) -> Unit,
    searchQuery: String,

    ) {

    TextField(
        value = searchQuery,
        onValueChange = { onSearch(it) },
        placeholder = {
            Text(
                text = TITLE_SEARCH_PLACEHOLDER,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        },
        singleLine = true,
        maxLines = 1,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = Constants.DESCRIPTION_SEARCH_ICON,
                tint = Color.White
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearch("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = DESCRIPTION_CLEAR_ICON,
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
    onIntent: (HomeIntent) -> Unit,
) {

    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    val isRecorded =
        uiState.audioState == AudioState.NOT_RECORDED || uiState.audioState == AudioState.INITIAL
    var isRecording by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF1C2317),
                shape = RoundedCornerShape(50.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    if (isRecorded) {
                        if (uiState.audioPermissionState.isRecordingAllowing) {
                            val outputFile = File(
                                context.filesDir,
                                "audio_recording_${System.currentTimeMillis()}.mp4"
                            )
                            ContextCompat.startForegroundService(
                                context,
                                AudioRecordingService.newIntent(context)
                            )
                            onIntent(StartRecording(outputFile))
                            isRecording = true
                        } else if (!uiState.audioPermissionState.isShowAudioPermissionDialog) {
                            onIntent(AudioDialog.ShowAudioPermissionDialog)
                        } else {
                            Toast.makeText(
                                context,
                                Constants.AUDIO_PERMISSION_ERROR_MESSAGE,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        onIntent(StopAndSendRecording)
                        isRecording = false
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF38452D),
                    contentColor = Color.White,
                ),
            ) {
                Icon(
                    painter = painterResource(id = if (isRecorded) R.drawable.microphone else R.drawable.vector_1),
                    contentDescription = DESCRIPTION_MICROPHONE_ICON,
                )

            }
        }
        if (isRecording && !uiState.loading) {
            RecordingWaveAnimation(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterVertically),
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
                    showSheet = true
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF38452D),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline),
                    contentDescription = DESCRIPTION_OUTLINE_ICON,
                )
            }
        }
        MyBottomSheet(showSheet) { showSheet = false }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheet(showSheet: Boolean, onDismiss: () -> Unit) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
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
                    TITLE_TEMPLATE,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(132.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2C3520)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier.padding(top = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF122505)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                TITLE_TEMPLATE_STUDY,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFFFFFFFF),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(132.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2C3520)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier.padding(top = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF122505)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                Constants.TITLE_TEMPLATE_WORK, color = Color(0xFFFFFFFF),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
                    contentDescription = Constants.DESCRIPTION_DELETE_ICON,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        content = {
            NoteCard(
                title = note.title,
                onFavoriteClick = { isFavorite ->
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

@Composable
fun RecordingWaveAnimation(modifier: Modifier = Modifier) {
    val waveAmplitude = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val waveHeight by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    LaunchedEffect(Unit) {
        waveAmplitude.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }

    Canvas(
        modifier = modifier
            .size(80.dp)
    ) {
        val centerY = size.height / 2
        val waveCount = 6
        val waveWidth = size.width / (waveCount * 2f)

        for (i in 0 until waveCount) {
            val x = i * waveWidth * 2
            val height = waveHeight * waveAmplitude.value * (1f - (i % 2) * 0.3f)
            drawRoundRect(
                color = Color.White.copy(alpha = waveAlpha),
                topLeft = Offset(x, centerY - height / 2),
                size = Size(waveWidth, height),
                cornerRadius = CornerRadius(50f, 50f)
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PreviewRecordingWaveAnimation() {
    RecordingWaveAnimation()
}*/

/*@Preview(showBackground = true)
@Composable
fun PreviewMainTopBar() {
    MainTopBar(searchQuery = "", onSearchQueryChange = {})
}*/

/*@Preview(showBackground = true)
@Composable
fun PreviewMainBottomBar() {
    val navController = rememberNavController()
    MainBottomBar(navController = navController)
}*/

/*@Preview(showBackground = true)
@Composable
fun PreviewMainFloatingButton() {
    val context = LocalContext.current
    MainFloatingButton(uiState = HomeUiState(), context = context, onIntent = {})
}*/

/*@Preview(showBackground = true)
@Composable
fun PreviewNoteList() {
    val navController = rememberNavController()
    NoteList(
        notes = listOf(
            Note(
                id = 1,
                title = "Заметка 1",
                createdAt = System.currentTimeMillis(),
                isFavorite = false,
                description = "df"
            ),
            Note(
                id = 2,
                title = "Заметка 2",
                createdAt = System.currentTimeMillis(),
                isFavorite = true,
                description = "ff"
            )
        ),
        navController = navController,
        onIntent = {},
        paddingValues = PaddingValues()
    )
}*/

/*@Preview(showBackground = true)
@Composable
fun PreviewNoteItem() {
    val navController = rememberNavController()
    NoteItem(
        note = Note(
            id = 1,
            title = "Пример заметки",
            createdAt = System.currentTimeMillis(),
            isFavorite = true,
            description = "ff"
        ),
        onCopyClick = {},
        navController = navController,
        onIntent = {}
    )
}*/

