package com.example.noteai.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.noteai.presentation.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopBar(title: String) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = Typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainTopBar() {
    TitleTopBar("Избранное")
}