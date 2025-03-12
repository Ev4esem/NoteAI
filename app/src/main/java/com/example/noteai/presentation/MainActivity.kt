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

class MainActivity : ComponentActivity() {
    private val mainViewModel: HomeViewModel by viewModel()
    private val favouriteViewModel: FavouriteViewModel by viewModel()
    private val noteViewModel: NoteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAITheme {
                val navController = rememberNavController()
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
