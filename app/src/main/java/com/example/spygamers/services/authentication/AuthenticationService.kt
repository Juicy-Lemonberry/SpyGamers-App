package com.example.spygamers.services.authentication

import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {
    @POST("http://spygamers.servehttp.com:44414/app-api/account/register")
    suspend fun registerUser(
        @Body user: UserRegistrationBody
    ): Response<StatusOnlyResponse>

    @POST("http://spygamers.servehttp.com:44414/app-api/account/login")
    suspend fun userLogin(
        @Body user: UserLoginBody
    ): Response<LoginResponse>

    /**
     * Use this service call to verify a authentication token...
     */
    @POST("http://spygamers.servehttp.com:44414/app-api/account/check-authentication")
    suspend fun checkAuthentication(
        @Body user: AuthOnlyBody
    ): Response<AuthenticationCheckResponse>
}