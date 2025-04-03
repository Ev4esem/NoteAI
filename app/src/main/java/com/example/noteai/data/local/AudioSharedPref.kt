package com.example.noteai.data.local

import android.content.Context

fun getUnsentAudioPath(context: Context): String? {
    val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
    return prefs.getString("unsent_audio_path", null)
}