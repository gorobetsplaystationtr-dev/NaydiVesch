package com.example.naydivesch.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.example.naydivesch.model.ThingLocationLink

@Database(
    entities = [Thing::class, Location::class, ThingLocationLink::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thingDao(): ThingDao
    abstract fun locationDao(): LocationDao
    abstract fun thingLocationLinkDao(): ThingLocationLinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "naydivesch.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}