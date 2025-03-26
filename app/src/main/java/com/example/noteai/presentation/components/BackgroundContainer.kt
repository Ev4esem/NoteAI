package com.example.noteai.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.noteai.R
import com.example.noteai.utils.Constants.BACKGROUND_IMAGE

@Composable
fun BackgroundContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = BACKGROUND_IMAGE,
            contentScale = ContentScale.Crop,
        )
        content()
    }
}
