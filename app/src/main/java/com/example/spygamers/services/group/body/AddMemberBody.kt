package com.example.spygamers.services.group.body

import com.google.gson.annotations.SerializedName

data class AddMemberBody (
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("group_id")
    val groupID: Int,
    @SerializedName("target_user_id")
    val targetUserID: Int
)