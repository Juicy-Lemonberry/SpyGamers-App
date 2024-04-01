package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.ConversationActivity

data class GetLatestConversationsResponse(
    val status: String,
    val result: List<ConversationActivity>
)