package com.example.spygamers.models

data class RecommendedFriend(
    val id : String,
    val username: String,
    val game_preference_weightage: Int,
    val same_group_weightage: Int,
    val timezone_weightage: Int
)