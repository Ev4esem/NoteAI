package com.example.noteai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteai.presentation.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBar(currentScreen: String) {
    TopAppBar(
        modifier = Modifier.background(Color.Gray),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentScreen,
                    style = Typography.titleLarge,
                    color = Color.Black
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainTopBar() {
    TitleTopBar("Избранное")
}