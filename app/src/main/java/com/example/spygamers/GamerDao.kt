package com.example.spygamers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GamerDao {
    // Insert or update the session token
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSessionToken(gamer: Gamer)

    // Get the session token from the local database
    @Query("SELECT sessionToken FROM session_token LIMIT 1")
    fun getSessionToken(): Flow<String?>
}