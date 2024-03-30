package com.example.spygamers.services.group.body

import com.google.gson.annotations.SerializedName

data class GetGroupMessagesBody (
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("group_id")
    val groupID: Int,
    @SerializedName("chunk_size")
    val chunkSize: Int = 25,
    @SerializedName("start_id")
    val startID: Int? = null
)