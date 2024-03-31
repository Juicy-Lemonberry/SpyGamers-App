package com.example.spygamers.services.recommendations

import com.google.gson.annotations.SerializedName

data class FriendsRecommendationBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("sort_by")
    val sortBy: String = "DEFAULT",
    @SerializedName("chunk_size")
    val chunkSize: Int = 10
)

data class FriendRequestBody(
    @SerializedName("auth_token")
    val authToken: String,

    @SerializedName("target_account_id")
    val targetAccountID: Int,
)