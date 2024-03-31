package com.example.spygamers.services.recommendations

import com.example.spygamers.models.RecommendedGroup

data class GroupRecommendationResponse(
    val status: String,
    val result: List<RecommendedGroup>
)