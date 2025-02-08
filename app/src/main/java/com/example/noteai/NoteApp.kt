package com.example.noteai

import android.app.Application
import com.example.noteai.di.initKoin
import org.koin.android.ext.koin.androidContext

class NoteApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NoteApp)
        }
    }

}