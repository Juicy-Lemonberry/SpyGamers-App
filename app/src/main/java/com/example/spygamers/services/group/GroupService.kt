package com.example.spygamers.services.group

import com.example.spygamers.API_BASE_URL
import com.example.spygamers.services.StatusOnlyResponse
import com.example.spygamers.services.group.body.AddMemberBody
import com.example.spygamers.services.group.body.CreateGroupBody
import com.example.spygamers.services.group.body.GetAccountGroupsBody
import com.example.spygamers.services.group.body.GetGroupMessagesBody
import com.example.spygamers.services.group.body.JoinGroupBody
import com.example.spygamers.services.group.response.CreateGroupResponse
import com.example.spygamers.services.group.response.GetAccountGroupsResponse
import com.example.spygamers.services.group.response.GetGroupMessagesResponse
import com.example.spygamers.services.group.response.SendGroupMessageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GroupService {
    @POST("$API_BASE_URL/group/get-account-groups")
    suspend fun getAccountGroups(
        @Body user: GetAccountGroupsBody
    ): Response<GetAccountGroupsResponse>

    @POST("$API_BASE_URL/group/get-messages")
    suspend fun getGroupMessages(
        @Body body: GetGroupMessagesBody
    ): Response<GetGroupMessagesResponse>

    @POST("$API_BASE_URL/group/create")
    suspend fun createGroup(
        @Body body: CreateGroupBody
    ): Response<CreateGroupResponse>

    @Multipart
    @POST("$API_BASE_URL/group/send-message")
    suspend fun sendMessage(
        @Part authToken: MultipartBody.Part,
        @Part targetGroupID: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part attachments: MultipartBody.Part? = null
    ): Response<SendGroupMessageResponse>

    @POST("$API_BASE_URL/group/add-member")
    suspend fun addMember(
        @Body body: AddMemberBody
    ): Response<StatusOnlyResponse>

    @POST("$API_BASE_URL/group/join")
    suspend fun joinGroup(
        @Body body: JoinGroupBody
    ): Response<StatusOnlyResponse>
}