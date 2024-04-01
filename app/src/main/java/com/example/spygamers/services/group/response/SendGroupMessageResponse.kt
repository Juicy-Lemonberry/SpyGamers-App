package com.example.spygamers.services.group.response

import com.google.gson.annotations.SerializedName
import java.util.Date


data class CreatedGroupMessage (
    @SerializedName("message_id")
    val messageID: Int,
    @SerializedName("attachment_ids")
    val attachmentsID: List<Int>,
    @SerializedName("timestamp")
    val timestamp: Date
)

data class SendGroupMessageResponse (
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: CreatedGroupMessage
)