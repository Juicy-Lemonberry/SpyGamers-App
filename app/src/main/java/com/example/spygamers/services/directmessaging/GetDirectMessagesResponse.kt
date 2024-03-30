package com.example.spygamers.services.directmessaging

import com.example.spygamers.models.messaging.DirectMessage

data class GetDirectMessagesResponse(
    val status: String,
    val messages: List<DirectMessage>
)