package com.example.spygamers.services.group.response

import com.google.gson.annotations.SerializedName
import java.util.Date


data class CreatedGroupMessage (
    @SerializedName("message_id")
    val messageID: Int,
    @SerializedName("attachment_ids")
    val attachmentsID: List<Int>,
    @SerializedName("group_id")
    val timestamp: Date
)

data class SendGroupMessageResponse (
    val status: String,
    val result: CreatedGroupMessage
)