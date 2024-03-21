package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.UserAccount

data class GetProfileResponse(
    val status: String,
    val result: UserAccount?
)