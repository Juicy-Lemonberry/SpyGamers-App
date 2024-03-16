package com.example.spygamers

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo")
data class Gamer(
    @PrimaryKey val sessionToken: String,
    @ColumnInfo(name = "accountID") val accountID: Int,
    @ColumnInfo(name = "timezoneCode") val timezoneCode: String,
    @ColumnInfo(name = "username") val username: String
)