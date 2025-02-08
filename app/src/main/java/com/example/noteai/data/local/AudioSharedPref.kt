package com.example.noteai.data.local

import android.content.Context

// TODO Добавить константы https://github.com/Ev4esem/NoteAI/issues/6
fun saveUnsentAudioPath(context: Context, path: String) {
    val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        putString("unsent_audio_path", path)
        apply()
    }
}

fun getUnsentAudioPath(context: Context): String? {
    val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
    return prefs.getString("unsent_audio_path", null)
}

fun clearUnsentAudioPath(context: Context) {
    val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        remove("unsent_audio_path")
        apply()
    }
}