package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.noteai.presentation.favourite_screen.FavouriteScreen
import com.example.noteai.presentation.favourite_screen.FavouriteViewModel
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeViewModel
import com.example.noteai.presentation.home_screen.MainScreen
import com.example.noteai.presentation.note_screen.NoteScreen
import com.example.noteai.presentation.note_screen.NoteViewModel
import com.example.noteai.utils.Constants

sealed class NavRoute(val route: String) {
    data object Main : NavRoute("main_screen")
    data object Favourite : NavRoute("favourite_screen")
    data object Note : NavRoute("note_screen")
}

@Composable
fun NotesNavHost(
    mainViewModel: HomeViewModel,
    favouriteViewModel: FavouriteViewModel,
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    onIntent: (HomeIntent) -> Unit
) {
    NavHost(navController = navController, startDestination = NavRoute.Main.route) {
        composable(NavRoute.Main.route) {
            MainScreen(navController, viewModel = mainViewModel, onIntent = onIntent)
        }
        composable(NavRoute.Favourite.route) {
            FavouriteScreen(viewModel = favouriteViewModel)
        }
        composable(
            route = NavRoute.Note.route + "/{${Constants.NOTE_ID}}",
            arguments = listOf(
                navArgument(
                    name = Constants.NOTE_ID,
                    builder = {
                        type = NavType.LongType
                    }
                )
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong(Constants.NOTE_ID)
            NoteScreen(
                viewModel = noteViewModel,
                noteId = noteId,
            )
        }
    }
}
