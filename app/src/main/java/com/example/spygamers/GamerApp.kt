package com.example.spygamers

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.spygamers.db.GamerDatabase
import com.example.spygamers.db.GamerRepository
import kotlinx.coroutines.GlobalScope

class GamerApp : Application(){
    val GamerDao by lazy { GamerDatabase.getDatabase(this, GlobalScope).gamerDao()}
    val Context.dataStore by preferencesDataStore(
        name = "GridPreference"
    )
    val repository by lazy { GamerRepository(GamerDao, dataStore) }

}
