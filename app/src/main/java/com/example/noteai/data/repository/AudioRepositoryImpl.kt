package com.example.noteai.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.noteai.data.network.AudioApiService
import com.example.noteai.domain.repository.AudioRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class AudioRepositoryImpl(
    private val context: Context,
    private val api: AudioApiService,
    private val connectivityManager: ConnectivityManager
) : AudioRepository {

    override suspend fun uploadAudio(file: File): Result<Unit> {
        return if (isOnline()) {
            try {
                val response = api.uploadAudio(file.toMultipartBody())
                if (response.isSuccessful) {
                    clearUnsentAudioPath(context)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Server error"))
                }
            } catch (e: Exception) {
                saveUnsentAudioPath(context, file.absolutePath)
                Result.failure(e)
            }
        } else {
            saveUnsentAudioPath(context, file.absolutePath)
            Result.failure(IOException("No internet"))
        }
    }

    override fun getPendingAudio(): File? {
        return getUnsentAudioPath(context)?.let { File(it) }
    }

    private fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody: RequestBody = RequestBody.create(
            okhttp3.MediaType.parse("audio/*"), this
        )
        return MultipartBody.Part.createFormData("file", this.name, requestBody)
    }

    private fun saveUnsentAudioPath(context: Context, path: String) {
        val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("unsent_audio_path", path)
            apply()
        }
    }

    private fun getUnsentAudioPath(context: Context): String? {
        val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
        return prefs.getString("unsent_audio_path", null)
    }

    private fun clearUnsentAudioPath(context: Context) {
        val prefs = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            remove("unsent_audio_path")
            apply()
        }
    }
}
