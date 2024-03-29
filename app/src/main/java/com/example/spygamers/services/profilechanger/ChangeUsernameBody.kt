package com.example.spygamers.services.profilechanger

import com.google.gson.annotations.SerializedName

data class ChangeUsernameBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("new_username")
    val newUsername: String
)