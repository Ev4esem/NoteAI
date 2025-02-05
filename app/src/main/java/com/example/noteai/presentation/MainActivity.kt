package com.example.noteai.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.noteai.di.initKoin
import com.example.noteai.presentation.ui.theme.NoteAITheme
import com.example.noteai.presentation.viewmodel.AudioViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: AudioViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        initKoin {
            androidContext(this@MainActivity)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAITheme {
                MainScreen(mainViewModel)
            }
        }
    }
}
