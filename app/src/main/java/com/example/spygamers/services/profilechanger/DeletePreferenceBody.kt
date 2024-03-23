package com.example.spygamers.services.profilechanger

import com.google.gson.annotations.SerializedName

data class DeletePreferenceBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("preference_id")
    val preferenceID: Int
)