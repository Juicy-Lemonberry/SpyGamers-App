package com.example.spygamers.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class GamePreference(
    @SerializedName("game_preference_id")
    val id: Int,

    @SerializedName("game_name")
    val name: String
)