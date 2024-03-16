package com.example.spygamers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

class GamerRepository(private val gamerDao: GamerDao, private val dataStore: DataStore<Preferences>) {

    // Function to insert or update the session token
    suspend fun insertOrUpdateSessionToken(sessionToken: String) {
        gamerDao.insertOrUpdateSessionToken(Gamer(sessionToken))
    }

    // Function to get the session token from the local database
    fun getSessionToken(): Flow<String?> {
        return gamerDao.getSessionToken()
    }
}