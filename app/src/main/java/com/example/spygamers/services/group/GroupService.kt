package com.example.spygamers.services.group

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupService {
    @POST("http://spygamers.servehttp.com:44414/app-api/group/get-account-groups")
    suspend fun getAccountGroups(
        @Body user: GetAccountGroupsBody
    ): Response<GetAccountGroupsResponse>
}