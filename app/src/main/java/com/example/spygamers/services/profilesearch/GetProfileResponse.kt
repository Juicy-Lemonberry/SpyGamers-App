package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.UserAccount
import com.example.spygamers.services.ResponseContract

data class GetProfileResponse(
    override val status: String,
    val result: UserAccount?
) : ResponseContract