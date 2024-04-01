package com.example.spygamers.services.profilesearch

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.AuthOnlyBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProfileSearchService {
    @GET("$API_BASE_URL/account/get-user-info")
    suspend fun getProfileInfo(
        @Query("account_id") accountID: Int
    ): Response<GetProfileResponse>

    @GET("$API_BASE_URL/account/get-game-preferences")
    suspend fun getGamePreferences(
        @Query("target_username_id") accountID: Int
    ): Response<GetGamePreferencesResponse>

    @POST("$API_BASE_URL/account/get-latest-conversations")
    suspend fun getLatestConversation(
        @Body body: AuthOnlyBody
    ): Response<GetLatestConversationsResponse>
}