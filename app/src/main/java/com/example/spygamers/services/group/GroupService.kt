package com.example.spygamers.services.group

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.group.body.CreateGroupBody
import com.example.spygamers.services.group.body.GetAccountGroupsBody
import com.example.spygamers.services.group.body.GetGroupMessagesBody
import com.example.spygamers.services.group.response.CreateGroupResponse
import com.example.spygamers.services.group.response.GetAccountGroupsResponse
import com.example.spygamers.services.group.response.GetGroupMessagesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupService {
    @POST("$API_BASE_URL/app-api/group/get-account-groups")
    suspend fun getAccountGroups(
        @Body user: GetAccountGroupsBody
    ): Response<GetAccountGroupsResponse>

    @POST("$API_BASE_URL/app-api/group/get-account-groups")
    suspend fun getGroupMessages(
        @Body body: GetGroupMessagesBody
    ): Response<GetGroupMessagesResponse>

    @POST("$API_BASE_URL/app-api/group/create")
    suspend fun createGroup(
        @Body body: CreateGroupBody
    ): Response<CreateGroupResponse>
}