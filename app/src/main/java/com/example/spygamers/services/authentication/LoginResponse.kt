package com.example.spygamers.services.authentication

data class LoginResponse(
    val status: String,
    val session_token: String? = null,
    val account_id: Int? = null,
    val timezone_code: String? = null
)
