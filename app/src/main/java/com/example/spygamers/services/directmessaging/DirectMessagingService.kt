package com.example.spygamers.services.directmessaging

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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
        @Part authToken: MultipartBody.Part,
        @Part targetAccountID: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part attachments: MultipartBody.Part? = null,
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/image/get-attachment")
    suspend fun getAttachment(
        @Body body: GetAttachmentBody
    ): Response<ResponseBody>
}

data class GetAttachmentBody(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("attachment_id")
    val attachmentID: Int,
    @SerializedName("attachment_type")
    val attachmentType: String
)