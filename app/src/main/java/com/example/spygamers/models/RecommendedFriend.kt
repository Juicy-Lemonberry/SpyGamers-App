package com.example.spygamers.models

data class RecommendedFriend(
    val id : Int,
    val username: String,
    val game_preference_weightage: Int,
    val same_group_weightage: Int,
    val timezone_weightage: Int
)