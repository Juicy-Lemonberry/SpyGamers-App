package com.example.spygamers.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

data class RetrofitResponse(
    val status: String,
    val session_token: String? = null,
    val account_id: Int? = null,
    val timezone_code: String? = null
)

data class UserRegistration(
    val username: String,
    val password: String,
    val email: String
)

data class UserLogin(
    val username: String,
    val password: String

)

data class GetRecommendationBody(
    val auth_token: String,
    val sort_by: String = "DEFAULT",
    val chunk_size: Int = 10
)

data class RecommendedFriend(
    val id : String,
    val username: String,
    val game_preference_weightage: Int,
    val same_group_weightage: Int,
    val timezone_weightage: Int
)

data class Recommendations(
    val status: String,
    val result: List<RecommendedFriend>
)

interface RecommendationService {
    //Reco List Stuff
    @POST("http://spygamers.servehttp.com:44414/app-api/recommend/friends")
    suspend fun getRecommendations(
        @Body user: GetRecommendationBody
    ): Response<Recommendations>
}

data class ChangeUsernameBody(
    val auth_token: String,
    val new_username: String
)

data class GetFriendsBody(
    val auth_token: String,
)

data class RemoveFriendBody(
    val target_account_id: Int,
    val auth_token: String
)

data class Friend(
    val account_id: Int,
    val username: String,
    val status: String,
)

data class Friends(
    val status: String,
    val friends: List<Friend>
)

interface AuthenticationService {
    @POST("http://spygamers.servehttp.com:44414/app-api/account/register")
    suspend fun registerUser(
        @Body user: UserRegistration
    ): Response<RetrofitResponse>

    @POST("http://spygamers.servehttp.com:44414/app-api/account/login")
    suspend fun userLogin(
        @Body user: UserLogin
    ): Response<RetrofitResponse>

    @PUT("http://spygamers.servehttp.com:44414/app-api/account/change-username")
    suspend fun changeUsername(
        @Body user: ChangeUsernameBody
    ): Response<RetrofitResponse>

}
interface FriendService {
    //Friend List Stuff
    @POST("http://spygamers.servehttp.com:44414/app-api/account/get-friends")
    suspend fun getFriends(
        @Body user: GetFriendsBody
    ): Response<Friends>

    @PUT("http://spygamers.servehttp.com:44414/app-api/account/remove-friend")
    suspend fun removeFriends(
        @Body user: RemoveFriendBody
    ): Response<RetrofitResponse>
}


