package com.example.noteai.data.network

import com.example.noteai.utils.Response
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST

// TODO подключить настоящий бэкенд https://github.com/Ev4esem/NoteAI/issues/5
interface AudioApiService {

    @Multipart
    @POST("upload/audio")
    suspend fun uploadAudio(@Body audio: MultipartBody.Part): Flow<Response>
}
