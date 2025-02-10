package com.example.noteai.domain.entity

import android.net.Uri

data class Recording(
    val id: Int,
    val title: String,
    val path: Uri,
    val timestamp: Long,
    val isProcessed: Boolean = false
)