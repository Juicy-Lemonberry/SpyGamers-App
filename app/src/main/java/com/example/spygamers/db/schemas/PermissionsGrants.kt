package com.example.spygamers.db.schemas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permissionGrants")
data class PermissionsGrants(
    @PrimaryKey val id: Int,
    val recommendationsGranted: Boolean = false,
    val mediaMessageGranted: Boolean = false
)