package com.example.spygamers

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RetrofitResponse(
    val status: String,
    val session_token: String? = null
)

data class User(
    val username: String,
    val password: String,
    val email: String
)

interface AuthenticationService {
    @POST("http://spygamers.servehttp.com:44414/app-api/account/register")
    suspend fun registerUser(
        @Body user: User
    ): Response<RetrofitResponse>
}

