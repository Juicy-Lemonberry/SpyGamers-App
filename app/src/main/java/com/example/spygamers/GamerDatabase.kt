package com.example.spygamers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Gamer::class], version = 3)
abstract class GamerDatabase : RoomDatabase() {
    abstract fun gamerDao(): GamerDao

    companion object {
        @Volatile
        private var INSTANCE: GamerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GamerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GamerDatabase::class.java,
                    "GamerDatabase"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}