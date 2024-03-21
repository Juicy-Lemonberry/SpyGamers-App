package com.example.spygamers.services.profilesearch

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileSearchService {
    @GET("http://spygamers.servehttp.com:44414/app-api/account/get-user-info")
    suspend fun getProfileInfo(
        @Query("account_id") accountID: Int
    ): Response<GetProfileResponse>

}