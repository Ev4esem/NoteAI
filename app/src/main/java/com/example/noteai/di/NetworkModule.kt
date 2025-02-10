package com.example.noteai.di

import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

private const val TIME_OUT = 60L

val networkModule = module {
    singleOf(::createOkHttpClient)
}

fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .build()
}