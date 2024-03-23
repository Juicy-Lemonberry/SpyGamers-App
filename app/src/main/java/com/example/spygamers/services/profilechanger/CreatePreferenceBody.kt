package com.example.spygamers.services.profilechanger

import com.google.gson.annotations.SerializedName

data class CreatePreferenceBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("game_name")
    val gameName: String
)