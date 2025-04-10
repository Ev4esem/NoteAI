package com.example.noteai.data.model

import com.google.gson.annotations.SerializedName

data class TaskResponse(
   @SerializedName("ID") val audioId: String,
   @SerializedName("State") val statusNote: String,
   @SerializedName("Content") val content: ContentResponse,
)

data class ContentResponse(
   @SerializedName("String") val string: String,
   @SerializedName("Valid") val valid: Boolean
)