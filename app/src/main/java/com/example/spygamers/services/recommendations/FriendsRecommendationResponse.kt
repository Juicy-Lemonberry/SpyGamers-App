package com.example.spygamers.services.recommendations

import com.example.spygamers.models.RecommendedFriend
import com.google.gson.annotations.SerializedName

data class FriendsRecommendationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: List<RecommendedFriend>
)