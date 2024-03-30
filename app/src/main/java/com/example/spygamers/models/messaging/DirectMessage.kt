package com.example.spygamers.models.messaging

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DirectMessage(
    @SerializedName("message_id")
    val messageID: Int,
    @SerializedName("sender_username")
    val senderUsername: String,
    @SerializedName("contact_username")
    val contactUsername: String,
    val content: String,
    val timestamp: Date,
    @SerializedName("attachments_id")
    val attachmentsID: List<Int>,
    @SerializedName("is_deleted")
    val isDeleted: Boolean
)