package com.example.spygamers.services.friendship

import com.example.spygamers.services.AuthOnlyBody
import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface FriendshipService {
    //Friend List Stuff
    @POST("http://spygamers.servehttp.com:44414/app-api/account/get-friends")
    suspend fun getFriends(
        @Body user: AuthOnlyBody
    ): Response<Friends>

    @PUT("http://spygamers.servehttp.com:44414/app-api/account/remove-friend")
    suspend fun removeFriends(
        @Body user: RemoveFriendBody
    ): Response<StatusOnlyResponse>
}
