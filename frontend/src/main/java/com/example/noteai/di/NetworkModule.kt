package com.example.noteai.di

import com.example.noteai.data.network.NoteApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://62.109.23.215:8005/"

val networkModule = module {
    singleOf(::createRetrofit)
    singleOf(::createNoteApi)
}

fun createNoteApi(): NoteApi = createRetrofit().create(NoteApi::class.java)

fun createRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}