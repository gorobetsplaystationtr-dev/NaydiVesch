package com.example.naydivesch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.naydivesch.data.model.History
import com.example.naydivesch.data.model.Link
import com.example.naydivesch.data.model.Location
import com.example.naydivesch.data.model.Thing

@Database(
    entities = [
        Thing::class,
        Location::class,
        Link::class,
        History::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NaydiVeschDatabase : RoomDatabase() {
    abstract fun thingDao(): ThingDao
    abstract fun locationDao(): LocationDao
    abstract fun linkDao(): LinkDao
    abstract fun historyDao(): HistoryDao
}