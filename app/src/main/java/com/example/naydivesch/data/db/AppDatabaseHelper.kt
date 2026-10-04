package com.example.naydivesch.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.naydivesch.model.Thing
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.ThingLocationLink

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, "naydivesch.db", null, 1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE things (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT DEFAULT '',
                category TEXT DEFAULT '',
                photoUrl TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                serverId INTEGER,
                UNIQUE(name)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT DEFAULT '',
                nfcUid TEXT,
                qrCodeUrl TEXT,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                serverId INTEGER,
                UNIQUE(name)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE thing_location_links (
                thingId INTEGER NOT NULL,
                locationId INTEGER NOT NULL,
                linkedAt INTEGER NOT NULL,
                active INTEGER NOT NULL DEFAULT 1,
                serverId INTEGER,
                PRIMARY KEY (thingId, locationId),
                FOREIGN KEY (thingId) REFERENCES things(id) ON DELETE CASCADE,
                FOREIGN KEY (locationId) REFERENCES locations(id) ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX idx_things_name ON things(name)")
        db.execSQL("CREATE INDEX idx_locations_name ON locations(name)")
        db.execSQL("CREATE INDEX idx_links_thing ON thing_location_links(thingId)")
        db.execSQL("CREATE INDEX idx_links_location ON thing_location_links(locationId)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For now, just recreate
        db.execSQL("DROP TABLE IF EXISTS thing_location_links")
        db.execSQL("DROP TABLE IF EXISTS things")
        db.execSQL("DROP TABLE IF EXISTS locations")
        onCreate(db)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseHelper? = null

        fun getInstance(context: Context): AppDatabaseHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
    }
}