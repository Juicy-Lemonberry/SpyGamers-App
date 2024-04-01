package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.GamePreference
import com.example.spygamers.services.ResponseContract
import com.google.gson.annotations.SerializedName

data class GetGamePreferencesResponse(
    @SerializedName("status")
    override val status: String,
    @SerializedName("preferences")
    val preferences: List<GamePreference>
) : ResponseContract