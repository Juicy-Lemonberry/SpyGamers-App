package com.example.spygamers.services.group.response

import com.example.spygamers.models.Group
import com.google.gson.annotations.SerializedName

data class GetAccountGroupsResponse (
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: List<Group>
)