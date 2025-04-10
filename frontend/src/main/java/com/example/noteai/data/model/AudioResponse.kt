package com.example.noteai.data.model

import com.google.gson.annotations.SerializedName


data class AudioResponse (
   @SerializedName("audio_id") val audioId: String,
   @SerializedName("status") val status: String,
   @SerializedName("message") val message: String
)
