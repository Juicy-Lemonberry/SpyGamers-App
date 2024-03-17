package com.example.spygamers.services.recommendations

data class FriendsRecommendationBody(
    val auth_token: String,
    val sort_by: String = "DEFAULT",
    val chunk_size: Int = 10
)