package com.example.noteai.presentation.navigation

import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.noteai.presentation.components.BackgroundContainer
import com.example.noteai.presentation.components.BottomNavigationBar
import com.example.noteai.presentation.favourite_screen.FavouriteIntent
import com.example.noteai.presentation.favourite_screen.FavouriteScreen
import com.example.noteai.presentation.favourite_screen.FavouriteViewModel
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeViewModel
import com.example.noteai.presentation.home_screen.MainFloatingButton
import com.example.noteai.presentation.home_screen.MainScreen
import com.example.noteai.presentation.note_screen.NoteScreen
import com.example.noteai.presentation.note_screen.NoteViewModel
import com.example.noteai.utils.Constants

sealed class NavRoute(val route: String) {
    data object Main : NavRoute(Constants.SCREEN_MAIN)
    data object Favourite : NavRoute(Constants.SCREEN_FAVOURITE)
    data object Note : NavRoute(Constants.SCREEN_NOTE)
}

@Composable
fun NotesNavHost(
    mainViewModel: HomeViewModel,
    favouriteViewModel: FavouriteViewModel,
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    onHomeIntent: (HomeIntent) -> Unit,
    onFavouriteIntent: (FavouriteIntent) -> Unit
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isNoteScreen = currentRoute?.startsWith(NavRoute.Note.route) == true
    val mainUiState by mainViewModel.uiState.collectAsState()
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (!isNoteScreen) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == NavRoute.Main.route) {
                MainFloatingButton(
                    uiState = mainUiState,
                    onIntent = onHomeIntent,
                    amplitudes = mainViewModel.amplitudes,
                )
            }
        }
    ) { paddingValues ->
        BackgroundContainer {
            NavHost(navController = navController, startDestination = NavRoute.Main.route) {
                composable(NavRoute.Main.route) {
                    MainScreen(
                        navController = navController,
                        viewModel = mainViewModel,
                        onIntent = onHomeIntent,
                        paddingValues = paddingValues
                    )
                }
                composable(NavRoute.Favourite.route) {
                    FavouriteScreen(
                        viewModel = favouriteViewModel,
                        navController = navController,
                        onIntent = onFavouriteIntent,
                        paddingValues = paddingValues
                    )
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
                        navController = navController,
                    )
                }
            }
        }
    }
}
