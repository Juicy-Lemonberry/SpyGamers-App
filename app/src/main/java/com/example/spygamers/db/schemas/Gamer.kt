package com.example.spygamers.db.schemas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo")
data class Gamer(
    @PrimaryKey val sessionToken: String
)