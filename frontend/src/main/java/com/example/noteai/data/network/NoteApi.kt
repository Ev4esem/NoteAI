package com.example.noteai.data.network

import com.example.noteai.data.model.AudioResponse
import com.example.noteai.data.model.TaskResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NoteApi {
    @Multipart
    @POST("audio")
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part
    ): AudioResponse

    @POST("tasks")
    suspend fun getTasksByNoteIds(
        @Body request: List<String>
    ): List<TaskResponse>

}