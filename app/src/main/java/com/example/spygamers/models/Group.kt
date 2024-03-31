package com.example.spygamers.models

import com.google.gson.annotations.SerializedName

data class Group(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("is_public")
    val isPublic: Boolean
)