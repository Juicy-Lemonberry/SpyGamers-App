package com.example.spygamers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GamerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(gamer: Gamer)

    @Delete
    suspend fun deleteUser(gamer: Gamer)

    @Update
    suspend fun updateUser(gamer: Gamer)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<Gamer>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean
}