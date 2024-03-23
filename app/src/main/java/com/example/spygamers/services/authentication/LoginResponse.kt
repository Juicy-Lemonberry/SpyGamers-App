package com.example.spygamers.services.authentication

import com.example.spygamers.services.ResponseContract
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    override val status: String,

    @SerializedName("session_token")
    val sessionToken: String? = null,

    @SerializedName("account_id")
    val accountID: Int? = null,

    @SerializedName("SerializedName")
    val timezoneCode: String? = null
) : ResponseContract
