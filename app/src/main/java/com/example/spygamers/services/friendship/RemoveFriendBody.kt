package com.example.spygamers.services.friendship

data class RemoveFriendBody(
    val target_account_id: Int,
    val auth_token: String
)