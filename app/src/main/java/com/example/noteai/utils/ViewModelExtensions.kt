package com.example.noteai.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun ViewModel.launchSafe(
    onError: ((Throwable) -> Unit)? = null,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        if (onError != null) {
            onError(throwable)
        } else {
            Log.e("ViewModelError", handlerError(throwable))
        }
    }
    viewModelScope.launch(
        context = defaultDispatcher + coroutineExceptionHandler,
        block = block
    )
}