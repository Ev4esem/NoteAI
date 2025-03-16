package com.example.noteai.presentation.home_screen

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.noteai.R
import com.example.noteai.data.repository.AudioRecordingService
import com.example.noteai.domain.entity.Note
import com.example.noteai.presentation.home_screen.HomeIntent.*
import com.example.noteai.presentation.navigation.NavRoute
import com.example.noteai.presentation.permission_dialog.AudioRecorderPermissionScreen
import com.example.noteai.utils.DateUtils
import com.example.noteai.utils.ObserveEffect
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onIntent: (HomeIntent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permission = Manifest.permission.RECORD_AUDIO
    var searchQuery by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            MainTopBar(searchQuery) { searchQuery = it }
        },
        bottomBar = {
            MainBottomBar(navController)
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MainFloatingButton(uiState, context, onIntent)
            }
        },
        content = { paddingValues ->
            NoteList(uiState.notes, navController, onIntent, paddingValues)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Домашний экран",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        )
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Поиск", color = Color.White) },
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Поиск",
                    tint = Color.White
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF747D6C),
                unfocusedContainerColor = Color(0xFF747D6C),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}


@Composable
fun MainBottomBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color(0xFF1F1F1F),
        contentColor = Color(0xFF808080),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(NavRoute.Note.route) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.subtract),
                        contentDescription = "Записи",
                    )
                }
                Text(
                    text = "Записи",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(NavRoute.Favourite.route) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vector),
                        contentDescription = "Избранное",
                    )
                }
                Text(
                    text = "Избранное",
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


@Composable
fun MainFloatingButton(uiState: HomeUiState, context: Context, onIntent: (HomeIntent) -> Unit) {
    val isRecorded =
        uiState.audioState == AudioState.NOT_RECORDED || uiState.audioState == AudioState.INITIAL
    var isRecording by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF1C2317),
                shape = RoundedCornerShape(30.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .padding(10.dp)
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
                            "Разрешите доступ к микрофону",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    onIntent(StopAndSendRecording)
                    isRecording = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38452D)),
            shape = CircleShape
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(10.dp),
                    color = Color.White
                )
            } else {
                Icon(
                    painter = painterResource(id = if (isRecorded) R.drawable.microphone else R.drawable.vector_1),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        if (isRecording) {
            RecordingWaveAnimation(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterVertically),
            )
        } else {
            Button(
                modifier = Modifier
                    .padding(10.dp)
                    .size(56.dp),
                onClick = {},
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38452D))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}


@Composable
fun NoteList(
    notes: List<Note>,
    navController: NavController,
    onIntent: (HomeIntent) -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(top = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note, {}, navController, onIntent
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onCopyClick: () -> Unit,
    navController: NavController,
    onIntent: (HomeIntent) -> Unit
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> false
                SwipeToDismissBoxValue.EndToStart -> {
                    onIntent(DeleteNote(note.id))
                    Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> true
            }
        },
        positionalThreshold = { it * .25f }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val progress = dismissState.progress
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF1744)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(12.dp, 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                onClick = {
                    navController.navigate(NavRoute.Note.route + "/${note.id}")
                },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCopyClick) {
                        Icon(
                            painter = painterResource(R.drawable.icon_copy),
                            contentDescription = "Copy",
                            tint = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = note.title,
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = DateUtils.formatDate(note.createdAt),
                                fontSize = 10.sp,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = { onIntent(ChangeFavoriteStatus(note.id)) }) {
                        Icon(
                            painter = painterResource(if (note.isFavorite) R.drawable.icon_red else R.drawable.save),
                            contentDescription = "Favorite",
                            tint = if (note.isFavorite) Color(0xFFD22B2B) else Color.Black
                        )
                    }
                }
            }
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

@Preview(showBackground = true)
@Composable
fun PreviewRecordingWaveAnimation() {
    RecordingWaveAnimation()
}

@Preview(showBackground = true)
@Composable
fun PreviewMainTopBar() {
    MainTopBar(searchQuery = "", onSearchQueryChange = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewMainBottomBar() {
    val navController = rememberNavController()
    MainBottomBar(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun PreviewMainFloatingButton() {
    val context = LocalContext.current
    MainFloatingButton(uiState = HomeUiState(), context = context, onIntent = {})
}

@Preview(showBackground = true)
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
}

@Preview(showBackground = true)
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
}

