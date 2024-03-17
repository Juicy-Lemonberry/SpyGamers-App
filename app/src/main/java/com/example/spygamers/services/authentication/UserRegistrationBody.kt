package com.example.spygamers.services.authentication

data class UserRegistrationBody(
    val username: String,
    val password: String,
    val email: String
)

