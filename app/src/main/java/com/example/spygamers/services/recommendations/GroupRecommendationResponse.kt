package com.example.spygamers.services.recommendations

import com.example.spygamers.models.RecommendedGroup
import com.google.gson.annotations.SerializedName

data class GroupRecommendationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: List<RecommendedGroup>
)