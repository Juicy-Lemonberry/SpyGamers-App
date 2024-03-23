package com.example.spygamers.services

import com.google.gson.annotations.SerializedName

data class AuthOnlyBody (
    @SerializedName("auth_token")
    override val authToken: String,
) : AuthTokenContract