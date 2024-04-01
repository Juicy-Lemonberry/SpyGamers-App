package com.example.spygamers.services.friendship

import com.google.gson.annotations.SerializedName

data class RemoveFriendBody(
    @SerializedName("target_account_id")
    val target_account_id: Int,
    @SerializedName("auth_token")
    val auth_token: String
)

data class AddFriendBody(
    @SerializedName("target_account_id")
    val target_account_id: Int,
    @SerializedName("auth_token")
    val auth_token: String
)