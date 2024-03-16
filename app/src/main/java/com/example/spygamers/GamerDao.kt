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
    // Insert or update the entire Gamer data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGamer(gamer: Gamer)

    // Retrieve the account ID
    @Query("SELECT accountID FROM userInfo LIMIT 1")
    fun getAccountId(): Flow<Int?>

    // Retrieve the session token
    @Query("SELECT sessionToken FROM userInfo LIMIT 1")
    fun getSessionToken(): Flow<String?>

    // Retrieve the timezone code
    @Query("SELECT timezoneCode FROM userInfo LIMIT 1")
    fun getTimezoneCode(): Flow<String?>

    // Retrieve the username
    @Query("SELECT username FROM userInfo LIMIT 1")
    fun getUsername(): Flow<String?>
}