package com.example.spygamers

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RegistrationResponse(
    val status: String,
    val session_token: String? = null
)

interface AuthenticationService {
    @POST("http://34.142.246.82:44414/account/register")
    suspend fun registerUser(
        @Body user: HashMap<String, String>
    ): Response<RegistrationResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://34.142.246.82:44414"

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthenticationService by lazy {
        retrofitInstance.create(AuthenticationService::class.java)
    }
}