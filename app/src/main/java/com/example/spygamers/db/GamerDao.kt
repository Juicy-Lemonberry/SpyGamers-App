package com.example.spygamers.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spygamers.db.schemas.Gamer
import kotlinx.coroutines.flow.Flow

@Dao
interface GamerDao {
    // Insert or update the entire Gamer data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGamer(gamer: Gamer)

    // Retrieve the account ID
    @Query("SELECT accountID FROM userInfo LIMIT 1")
    fun getAccountId(): Flow<Int?>

    // Retrieve the session token
    @Query("SELECT sessionToken FROM userInfo LIMIT 1")
    fun getSessionToken(): Flow<String?>
}