package com.example.noteai.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.noteai.presentation.favourite_screen.FavouriteViewModel
import com.example.noteai.presentation.home_screen.HomeViewModel
import com.example.noteai.presentation.navigation.NotesNavHost
import com.example.noteai.presentation.note_screen.NoteViewModel
import com.example.noteai.presentation.ui.theme.NoteAITheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAITheme {
                val navController = rememberNavController()
                val mainViewModel: HomeViewModel by viewModel()
                val favouriteViewModel: FavouriteViewModel by viewModel()
                val noteViewModel by viewModel<NoteViewModel> {
                    parametersOf(navController.currentBackStackEntry?.savedStateHandle)
                }
                NotesNavHost(
                    mainViewModel = mainViewModel,
                    favouriteViewModel = favouriteViewModel,
                    noteViewModel = noteViewModel,
                    navController = navController,
                    onIntent = mainViewModel::handlerIntent
                )
            }
        }
    }
}
