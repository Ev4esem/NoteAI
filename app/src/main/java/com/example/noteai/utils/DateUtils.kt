package com.example.noteai.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(timesTamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale("ru", "RU"))
        val formattedDate = sdf.format(Date(timesTamp))
        return formattedDate.replaceFirstChar { it.uppercase() }
    }
}