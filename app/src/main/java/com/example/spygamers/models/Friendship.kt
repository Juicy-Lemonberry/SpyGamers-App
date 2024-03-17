package com.example.spygamers.models


/**
 *
 * @param status Can either be `INCOMING_REQUEST`, or `OUTGOING_REQUEST` or `ACCEPTED`
 */
data class Friendship(
    val account_id: Int,
    val username: String,
    val status: String,
)