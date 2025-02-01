package com.example.noteai.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.noteai.data.local.db.DataBaseConstants
import com.example.noteai.data.local.db.NoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideSqliteDriverFactory(@ApplicationContext context: Application): SqlDriver {
        return AndroidSqliteDriver(NoteDataBase.Schema, context, DataBaseConstants.name)
    }

    @Provides
    fun provideNoteDataBase(sqlDriver: SqlDriver): NoteDataBase {
        val database = NoteDataBase(sqlDriver)
        return database
    }
}
