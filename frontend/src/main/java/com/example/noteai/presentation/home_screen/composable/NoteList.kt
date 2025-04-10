package com.example.noteai.presentation.home_screen.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noteai.R
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.entity.StatusNote
import com.example.noteai.presentation.components.NoteCard
import com.example.noteai.presentation.components.NoteCardError
import com.example.noteai.presentation.components.NoteCardWait
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeIntent.ChangeFavoriteStatus
import com.example.noteai.presentation.home_screen.HomeIntent.DeleteNote
import com.example.noteai.presentation.navigation.NavRoute

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
        items(
            items = notes,
            key = Note::id,
        ) { note ->
            NoteItem(
                modifier = Modifier.animateItem(),
                note = note,
                navController = navController,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    navController: NavController,
    onIntent: (HomeIntent) -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                onIntent(DeleteNote(note.id))
                return@rememberSwipeToDismissBoxState true
            } else {
                return@rememberSwipeToDismissBoxState false
            }
        },
    )
    SwipeToDismissBox(
        modifier = modifier,
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
            when(note.status) {
                StatusNote.WAIT -> {
                    NoteCardWait(
                        title = note.title,
                        createdAt = note.createdAt,
                    )
                }
                StatusNote.READY -> {
                    NoteCard(
                        title = note.title,
                        onFavoriteClick = {
                            onIntent(ChangeFavoriteStatus(note.id))
                        },
                        onClick = {
                            navController.navigate(NavRoute.Note.route + "/${note.id}")
                        },
                        isFavorite = note.isFavorite,
                        createdAt = note.createdAt
                    )
                }
                StatusNote.ERROR -> {
                    NoteCardError(
                        title = note.title,
                        createdAt = note.createdAt,
                    )
                }
            }
        }
    )
}