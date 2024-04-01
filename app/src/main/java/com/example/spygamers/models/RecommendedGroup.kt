package com.example.spygamers.models

import com.google.gson.annotations.SerializedName

data class RecommendedGroup(
    @SerializedName("id")
    val id : Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("member_count")
    val memberCount: Int,
    @SerializedName("weight")
    val weight: Float
)