package com.example.spygamers.services.group.response

import com.example.spygamers.models.messaging.GroupMessage
import com.google.gson.annotations.SerializedName

data class GetGroupMessagesResponse (
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: List<GroupMessage>
)