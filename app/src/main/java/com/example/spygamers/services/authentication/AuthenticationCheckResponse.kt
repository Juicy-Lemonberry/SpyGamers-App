package com.example.spygamers.services.authentication

import java.util.Date

data class AuthenticationCheckResponse(
    val status: String,
    val result: FullAccountData
)

data class FullAccountData(
    val id: Int,
    val username: String,
    val email: String,
    val created_at: Date,
    val timezone_code: String
)