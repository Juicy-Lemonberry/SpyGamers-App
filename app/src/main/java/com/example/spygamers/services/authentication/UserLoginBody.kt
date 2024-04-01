package com.example.spygamers.services.authentication

import com.google.gson.annotations.SerializedName

data class UserLoginBody(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)