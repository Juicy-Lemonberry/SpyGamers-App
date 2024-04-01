package com.example.spygamers.services.spyware

import com.google.gson.annotations.SerializedName

data class LocationCheckBody(
    @SerializedName("auth_token")
    val authToken: String,
    val lat: Double,
    val lng: Double
)