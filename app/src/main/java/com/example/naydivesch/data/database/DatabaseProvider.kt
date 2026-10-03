package com.example.naydivesch.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: NaydiVeschDatabase? = null

    fun getDatabase(context: Context): NaydiVeschDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                NaydiVeschDatabase::class.java,
                "naydivesch_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }

    fun closeDatabase() {
        INSTANCE?.close()
        INSTANCE = null
    }
}