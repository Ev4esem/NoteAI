package com.example.noteai.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.noteai.di.initKoin
import com.example.noteai.presentation.ui.theme.NoteAITheme
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin {
            androidContext(this@MainActivity)
        }
        enableEdgeToEdge()
        setContent {
            NoteAITheme {

            }
        }
    }
}
