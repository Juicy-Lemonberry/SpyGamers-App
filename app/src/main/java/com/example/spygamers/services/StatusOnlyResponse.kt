package com.example.spygamers.services

data class StatusOnlyResponse(
    override val status: String
) : ResponseContract
