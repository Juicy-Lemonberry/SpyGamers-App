package com.example.spygamers.services.group.body

import com.google.gson.annotations.SerializedName

data class CreateGroupBody (
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("group_name")
    val groupName: String,
    @SerializedName("group_description")
    val groupDescription: String? = null,
    @SerializedName("is_public")
    val isPublic: Boolean = false
)