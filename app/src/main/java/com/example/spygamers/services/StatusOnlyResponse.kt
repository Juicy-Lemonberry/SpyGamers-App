package com.example.spygamers.services

import com.google.gson.annotations.SerializedName

data class StatusOnlyResponse(
    @SerializedName("status")
    override val status: String
) : ResponseContract
