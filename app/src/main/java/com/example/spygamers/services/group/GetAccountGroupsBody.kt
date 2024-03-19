package com.example.spygamers.services.group

data class GetAccountGroupsBody (
    val auth_token: String,
    val filter: String?
)