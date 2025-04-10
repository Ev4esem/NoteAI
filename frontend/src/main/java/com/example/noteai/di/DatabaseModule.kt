package com.example.noteai.di

import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.local.db.createDataBase
import com.example.noteai.data.local.db.sqlDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    factory { sqlDriverFactory() }
    single { createDataBase(sqlDriver = get()) }
    single { NoteDao(noteDataBase = get()) }
}