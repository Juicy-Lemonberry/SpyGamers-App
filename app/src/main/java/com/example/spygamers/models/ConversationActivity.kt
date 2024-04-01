package com.example.spygamers.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ConversationActivity (
    @SerializedName("conversation_id")
    val conversationID: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("latest_content")
    val content: String,
    @SerializedName("activity_date")
    val date: Date,
    @SerializedName("conversation_type")
    val type: String,
    @SerializedName("is_public_group")
    val isPublic: Boolean,
    @SerializedName("group_description")
    val groupDescription: String
)