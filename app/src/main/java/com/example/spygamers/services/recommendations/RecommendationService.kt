package com.example.spygamers.services.recommendations

import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationService {
    //Reco List Stuff
    @POST("http://spygamers.servehttp.com:44414/app-api/recommend/friends")
    suspend fun getRecommendations(
        @Body user: FriendsRecommendationBody
    ): Response<FriendsRecommendationResponse>

    @POST("http://spygamers.servehttp.com:44414/app-api/account/send-friend-request")
    suspend fun sendFriendRequest(
        @Body user: FriendRequestBody
    ): Response<StatusOnlyResponse>

}
