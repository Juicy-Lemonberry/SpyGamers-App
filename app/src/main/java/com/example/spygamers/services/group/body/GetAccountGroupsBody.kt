package com.example.spygamers.services.group.body

import com.google.gson.annotations.SerializedName

data class GetAccountGroupsBody (
    @SerializedName("auth_token")
    val authToken: String,
    val filter: String?
)