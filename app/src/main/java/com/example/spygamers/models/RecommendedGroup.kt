package com.example.spygamers.models

import com.google.gson.annotations.SerializedName

data class RecommendedGroup(
    val id : Int,
    val name: String,
    val description: String,
    @SerializedName("member_count")
    val memberCount: Int,
    val weight: Float
)