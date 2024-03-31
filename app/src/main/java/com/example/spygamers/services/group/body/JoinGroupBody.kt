package com.example.spygamers.services.group.body

import com.google.gson.annotations.SerializedName

data class JoinGroupBody (
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("group_id")
    val groupID: Int,
)