package com.example.spygamers.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spygamers.db.schemas.Gamer
import com.example.spygamers.db.schemas.PermissionsGrants
import kotlinx.coroutines.flow.Flow

@Dao
interface GamerDao {
    // Insert or update the entire Gamer data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGamer(gamer: Gamer)

    @Query("DELETE FROM userInfo")
    suspend fun deleteAllSessionTokens();

    // Retrieve the session token
    @Query("SELECT sessionToken FROM userInfo LIMIT 1")
    fun getSessionToken(): Flow<String?>

    @Query("SELECT * FROM permissionGrants LIMIT 1")
    fun getPermissionGrants(): Flow<PermissionsGrants?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePermissionGrants(permissions: PermissionsGrants)
}