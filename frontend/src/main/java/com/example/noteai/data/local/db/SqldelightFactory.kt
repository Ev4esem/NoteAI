package com.example.noteai.data.local.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

fun Scope.sqlDriverFactory(): SqlDriver {
    return AndroidSqliteDriver(NoteDataBase.Schema, androidContext(), DataBaseConstants.NAME)
}

fun createDataBase(sqlDriver: SqlDriver): NoteDataBase {
    return NoteDataBase(sqlDriver)
}