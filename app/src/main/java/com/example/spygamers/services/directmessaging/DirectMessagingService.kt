package com.example.spygamers.services.directmessaging

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DirectMessagingService {
    @POST("$API_BASE_URL/account/get-direct-messages")
    suspend fun getDirectMessages(
        @Body body: GetDirectMessagesBody
    ): Response<GetDirectMessagesResponse>

    @Multipart
    @POST("$API_BASE_URL/account/send-direct-message")
    suspend fun sendDirectMessage(
        @Part("auth_token") authToken: String,
        @Part("target_account_id") targetAccountID: Int,
        @Part("content") content: String,
        @Part files: List<MultipartBody.Part>? = null,
    ): Response<StatusOnlyResponse>
}