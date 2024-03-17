package com.example.spygamers.services.friendship

import com.example.spygamers.models.Friendship

data class Friends(
    val status: String,
    val friends: List<Friendship>
)