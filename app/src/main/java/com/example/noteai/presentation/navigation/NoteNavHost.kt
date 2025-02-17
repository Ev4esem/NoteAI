package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeViewModel
import com.example.noteai.presentation.home_screen.MainScreen
import com.example.noteai.presentation.note_screen.NoteScreen

sealed class NavRoute(val route: String) {
    object Main : NavRoute("main_screen")
    object Note : NavRoute("note_screen")
}

@Composable
fun NotesNavHost(
    viewModel: HomeViewModel,
    navController: NavHostController,
    onIntent: (HomeIntent) -> Unit
) {
    NavHost(navController = navController, startDestination = NavRoute.Main.route) {
        composable(NavRoute.Main.route) {
            MainScreen(navController, viewModel = viewModel, onIntent = onIntent)
        }
        composable(NavRoute.Note.route + "/{Id}") { backStackEntry ->
            NoteScreen(
                navController = navController,
                viewModel = viewModel,
                noteId = backStackEntry.arguments?.getString("Id")
            )
        }
    }
}
