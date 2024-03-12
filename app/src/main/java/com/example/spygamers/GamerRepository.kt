package com.example.spygamers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class GamerRepository(private val gamerDao: GamerDao, private val dataStore: DataStore<Preferences>) {

    suspend fun insertUser(gamer: Gamer) = gamerDao.insertUser(gamer)

    suspend fun deleteUser(gamer: Gamer) = gamerDao.deleteUser(gamer)

    suspend fun updateUser(gamer: Gamer) = gamerDao.updateUser(gamer)

    suspend fun usernameExists(username: String): Boolean {
        return gamerDao.usernameExists(username)
    }

    suspend fun emailExists(email: String): Boolean {
        return gamerDao.emailExists(email)
    }
}