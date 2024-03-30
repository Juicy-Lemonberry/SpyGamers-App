package com.example.spygamers.services.group.response

import com.example.spygamers.models.messaging.GroupMessage

data class GetGroupMessagesResponse (
    val status: String,
    val result: List<GroupMessage>
)