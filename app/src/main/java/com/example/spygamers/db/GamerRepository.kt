package com.example.spygamers.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.spygamers.db.schemas.Gamer
import com.example.spygamers.db.schemas.PermissionsGrants
import kotlinx.coroutines.flow.Flow

class GamerRepository(private val gamerDao: GamerDao, private val dataStore: DataStore<Preferences>) {

    // Function to insert or update the session token
    suspend fun insertOrUpdateGamer(gamer: Gamer) {
        gamerDao.insertOrUpdateGamer(gamer)
    }

    suspend fun deleteSessionToken() {
        gamerDao.deleteAllSessionTokens();
    }

    // Function to get the session token from the local database
    fun getSessionToken(): Flow<String?> {
        return gamerDao.getSessionToken()
    }

    suspend fun insertOrUpdatePermissionStates(permissionsGrants: PermissionsGrants) {
        gamerDao.insertOrUpdatePermissionGrants(permissionsGrants)
    }

    fun getPermissionGrants(): Flow<PermissionsGrants?> {
        return gamerDao.getPermissionGrants()
    }
}