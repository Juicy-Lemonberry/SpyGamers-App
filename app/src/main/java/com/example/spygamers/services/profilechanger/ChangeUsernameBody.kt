package com.example.spygamers.services.profilechanger

data class ChangeUsernameBody(
    val auth_token: String,
    val new_username: String
)