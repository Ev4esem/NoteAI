package com.example.noteai.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST

interface AudioApiService {

    @Multipart
    @POST("upload/audio")
    suspend fun uploadAudio(@Body audio: MultipartBody.Part): Response<Unit>
}
