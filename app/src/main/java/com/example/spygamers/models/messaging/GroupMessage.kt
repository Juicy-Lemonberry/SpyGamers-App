package com.example.spygamers.models.messaging

import com.google.gson.annotations.SerializedName
import java.util.Date

data class GroupMessage(
    @SerializedName("message_id")
    val messageID: Int,
    @SerializedName("sender_id")
    val senderID: Int,
    @SerializedName("sender_username")
    val senderUsername: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("attachments_id")
    val attachmentsID: List<Int>,
    @SerializedName("is_deleted")
    val isDeleted: Boolean
)