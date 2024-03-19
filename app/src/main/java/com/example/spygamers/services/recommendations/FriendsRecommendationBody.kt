package com.example.spygamers.services.recommendations

data class FriendsRecommendationBody(
    val auth_token: String,
    val sort_by: String = "DEFAULT",
    val chunk_size: Int = 10
)

data class FriendRequestBody(
    val auth_token: String,
    val target_account_id: Int,
)