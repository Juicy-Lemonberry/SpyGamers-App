package com.example.spygamers.services.recommendations

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationService {
    @POST("$API_BASE_URL/recommend/friends")
    suspend fun getRecommendations(
        @Body user: FriendsRecommendationBody
    ): Response<FriendsRecommendationResponse>

    // TODO: Move this to friendship service...
    @POST("$API_BASE_URL/account/send-friend-request")
    suspend fun sendFriendRequest(
        @Body user: FriendRequestBody
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/recommend/groups")
    suspend fun getGroupRecommendations(
        @Body body: GroupRecommendationBody
    ): Response<GroupRecommendationResponse>
}
