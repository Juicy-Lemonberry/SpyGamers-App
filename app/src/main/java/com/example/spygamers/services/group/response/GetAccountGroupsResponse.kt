package com.example.spygamers.services.group.response

import com.example.spygamers.models.Group

data class GetAccountGroupsResponse (
    val status: String,
    val result: List<Group>
)