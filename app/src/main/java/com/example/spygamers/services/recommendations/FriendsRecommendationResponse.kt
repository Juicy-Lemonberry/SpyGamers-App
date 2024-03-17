package com.example.spygamers.services.recommendations

import com.example.spygamers.models.RecommendedFriend

data class FriendsRecommendationResponse(
    val status: String,
    val result: List<RecommendedFriend>
)