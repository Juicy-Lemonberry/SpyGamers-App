package com.example.spygamers.services.profilechanger

import com.example.spygamers.services.StatusOnlyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileChangerService {
    @PUT("http://spygamers.servehttp.com:44414/app-api/account/change-username")
    suspend fun changeUsername(
        @Body user: ChangeUsernameBody
    ): Response<StatusOnlyResponse>

    //#region Game Preference
    @POST("http://spygamers.servehttp.com:44414/app-api/account/add-game-preference")
    suspend fun createGamePreference(
        @Body user: CreatePreferenceBody
    ): Response<StatusOnlyResponse>

    @HTTP(method = "DELETE", path = "http://spygamers.servehttp.com:44414/app-api/account/delete-game-preference", hasBody = true)
    suspend fun deleteGamePreference(
        @Body user: DeletePreferenceBody
    ): Response<StatusOnlyResponse>
    //#endregion
}