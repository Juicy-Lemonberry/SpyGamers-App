package com.example.spygamers.services.authentication

import com.example.spygamers.services.ResponseContract
import com.google.gson.annotations.SerializedName
import java.util.Date

data class AuthenticationCheckResponse(
    override val status: String,
    val result: FullAccountData
) : ResponseContract

data class FullAccountData(
    val id: Int,
    val username: String,
    val email: String,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("timezone_code")
    val timezoneCode: String
)