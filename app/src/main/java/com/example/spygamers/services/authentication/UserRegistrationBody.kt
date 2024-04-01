package com.example.spygamers.services.authentication

import com.google.gson.annotations.SerializedName

data class UserRegistrationBody(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String
)

