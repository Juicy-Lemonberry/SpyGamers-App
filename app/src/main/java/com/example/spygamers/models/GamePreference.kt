package com.example.spygamers.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePreference(
    @SerializedName("game_preference_id")
    val id: Int,

    @SerializedName("game_name")
    val name: String
) : Parcelable