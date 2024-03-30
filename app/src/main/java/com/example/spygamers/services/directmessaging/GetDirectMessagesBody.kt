package com.example.spygamers.services.directmessaging

import com.google.gson.annotations.SerializedName

/**
 * Messages are fetched sorted by a timestamp, with the first index being the latest message.
 * The below two parameters can be used to do dynamic fetching during runtime, rather than attempting to fetch the entire message list all at once...
 *
 * @param chunkSize How much messages to fetch.
 * @param startID ID of which message to start fetching from. (The message with the same ID as 'startID' is ignored.)
 */
data class GetDirectMessagesBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("target_account_id")
    val targetAccountID: Int,
    @SerializedName("chunk_size")
    val chunkSize: Int = 25,
    @SerializedName("start_id")
    val startID: Int? = null
)