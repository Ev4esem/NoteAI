package com.example.noteai.utils

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

sealed interface Response {
    data class Success <T> (val data: T): Response
    data class Error(val message: String?): Response
    data object Loading: Response
}

fun handlerError(exception: Throwable): String {
    return when (exception) {
        is SocketTimeoutException -> "Превышено время ожидания ответа сервера"
        is UnknownHostException -> "Сервер недоступен. Проверьте подключение к интернету"
        is SSLHandshakeException -> "Ошибка безопасности соединения. Проверьте дату и время"
        is IOException -> "Ошибка сети. Проверьте интернет-соединение"
        is HttpException -> {
            when (val code = exception.code()) {
                400 -> "Некорректный запрос"
                401 -> "Требуется авторизация"
                403 -> "Доступ запрещён"
                404 -> "Ресурс не найден"
                413 -> "Аудио слишком большое (макс. 100 МБ)"
                415 -> "Неподдерживаемый формат аудио"
                429 -> "Слишком много запросов. Попробуйте позже"
                500 -> "Ошибка на сервере. Мы уже работаем над этим"
                502 -> "Проблемы с сервером. Повторите попытку"
                503 -> "Сервер временно недоступен"
                504 -> "Сервер не ответил вовремя"
                in 400..499 -> "Ошибка клиента ($code)"
                in 500..599 -> "Ошибка сервера ($code)"
                else -> "Неизвестная HTTP ошибка ($code)"
            }
        }
        is SecurityException -> "Разрешение на запись аудио не предоставлено"
        else -> "Неизвестная ошибка: ${exception.message}"
    }.also {
        Log.e("ErrorHandler", "Error: ${exception.javaClass.simpleName}", exception)
    }
}