package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.ConversationActivity
import com.google.gson.annotations.SerializedName

data class GetLatestConversationsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: List<ConversationActivity>
)