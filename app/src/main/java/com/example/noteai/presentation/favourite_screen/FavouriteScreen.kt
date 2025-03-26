package com.example.noteai.presentation.favourite_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.noteai.domain.entity.Note
import com.example.noteai.presentation.components.NoteCard
import com.example.noteai.presentation.components.TitleTopBar
import com.example.noteai.presentation.favourite_screen.FavouriteIntent.ChangeFavoriteStatus
import com.example.noteai.presentation.navigation.NavRoute
import com.example.noteai.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel,
    navController: NavHostController,
    onIntent: (FavouriteIntent) -> Unit,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        TitleTopBar(Constants.FAVOURITE_TITLE)
        NoteList(
            notes = uiState.favouriteNotes,
            navController = navController,
            onIntent = onIntent,
        )
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    navController: NavController,
    onIntent: (FavouriteIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(items = notes) { note ->
            NoteCard(
                title = note.title,
                onFavoriteClick = { isFavorite ->
                    onIntent(ChangeFavoriteStatus(note.id))
                },
                onClick = {
                    navController.navigate(NavRoute.Note.route + "/${note.id}")
                },
                isFavorite = note.isFavorite,
                createdAt = note.createdAt
            )
        }
    }
}