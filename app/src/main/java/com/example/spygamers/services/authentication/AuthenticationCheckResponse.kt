package com.example.spygamers.services.authentication

import com.example.spygamers.services.ResponseContract
import com.google.gson.annotations.SerializedName
import java.util.Date

data class AuthenticationCheckResponse(
    @SerializedName("status")
    override val status: String,
    @SerializedName("result")
    val result: FullAccountData
) : ResponseContract

data class FullAccountData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("timezone_code")
    val timezoneCode: String
)