package com.example.spygamers.models

import com.google.gson.annotations.SerializedName

enum class WeightageCategory {
    NONE,
    GAME_PREFERENCE,
    SAME_GROUP,
    TIMEZONE
}

data class RecommendedFriend(
    val id : Int,
    val username: String,

    @SerializedName("game_preference_weightage")
    val gamePreferenceWeightage: Float,

    @SerializedName("same_group_weightage")
    val sameGroupWeightage: Float,

    @SerializedName("timezone_weightage")
    val timezoneWeightage: Float
) {
    fun highestWeightage(): WeightageCategory {
        val weightages = mapOf(
            WeightageCategory.GAME_PREFERENCE to gamePreferenceWeightage,
            WeightageCategory.SAME_GROUP to sameGroupWeightage,
            WeightageCategory.TIMEZONE to timezoneWeightage
        )
        val maxEntry = weightages.maxByOrNull { it.value }
        return maxEntry?.key ?: WeightageCategory.NONE
    }
}