package com.example.spygamers

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_token")
data class Gamer(
    @PrimaryKey val sessionToken: String
)