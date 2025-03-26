package com.example.noteai.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formatter = SimpleDateFormat("dd MMM yyyy", Locale("ru", "RU"))

    fun formatDate(timestamp: Long?): String {
        return if (timestamp != null && timestamp > 0) {
            formatter.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
        } else {
            "Нет даты"
        }
    }
}