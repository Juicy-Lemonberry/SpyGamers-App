package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.GamePreference
import com.example.spygamers.services.ResponseContract

data class GetGamePreferencesResponse(
    override val status: String,
    val result: List<GamePreference>
) : ResponseContract