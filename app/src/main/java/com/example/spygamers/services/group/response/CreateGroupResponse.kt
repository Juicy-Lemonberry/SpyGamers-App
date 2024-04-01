package com.example.spygamers.services.group.response

import com.google.gson.annotations.SerializedName

data class CreateGroupResponse (
    @SerializedName("status")
    val status: String,
    @SerializedName("group_id")
    val groupID: Int
)