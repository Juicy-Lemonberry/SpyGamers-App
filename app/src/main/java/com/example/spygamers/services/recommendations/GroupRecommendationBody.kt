package com.example.spygamers.services.recommendations

import com.google.gson.annotations.SerializedName

data class GroupRecommendationBody (
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("chunk_size")
    val chunkSize: Int = 10
)