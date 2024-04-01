package com.example.spygamers.services.directmessaging

import com.example.spygamers.models.messaging.DirectMessage
import com.google.gson.annotations.SerializedName

data class GetDirectMessagesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("messages")
    val messages: List<DirectMessage>
)