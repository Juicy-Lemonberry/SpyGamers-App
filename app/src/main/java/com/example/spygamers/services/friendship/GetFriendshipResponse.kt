package com.example.spygamers.services.friendship

import com.example.spygamers.models.Friendship
import com.google.gson.annotations.SerializedName

data class Friends(
    @SerializedName("status")
    val status: String,
    @SerializedName("friends")
    val friends: List<Friendship>
)