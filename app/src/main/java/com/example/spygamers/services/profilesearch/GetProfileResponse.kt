package com.example.spygamers.services.profilesearch

import com.example.spygamers.models.UserAccount
import com.example.spygamers.services.ResponseContract
import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @SerializedName("status")
    override val status: String,
    @SerializedName("result")
    val result: UserAccount?
) : ResponseContract