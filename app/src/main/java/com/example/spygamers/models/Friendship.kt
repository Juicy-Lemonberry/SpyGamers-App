package com.example.spygamers.models

import com.google.gson.annotations.SerializedName


/**
 *
 * @param status Can either be `INCOMING_REQUEST`, or `OUTGOING_REQUEST` or `ACCEPTED`
 */
data class Friendship(
    @SerializedName("account_id")
    val accountID: Int,
    val username: String,
    val status: String,
)