package com.example.noteai.di


import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)  // Uses the application context
            androidLogger()  // For debugging
            modules(listOf(noteModule, databaseModule))  // Load your modules here
        }
    }
}
