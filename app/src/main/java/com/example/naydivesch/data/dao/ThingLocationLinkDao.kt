package com.example.naydivesch.data.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.naydivesch.data.db.AppDatabaseHelper
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.example.naydivesch.model.ThingLocationLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class ThingLocationLinkDao(private val dbHelper: AppDatabaseHelper) {

    private fun cursorToLink(cursor: Cursor): ThingLocationLink {
        return ThingLocationLink(
            thingId = cursor.getLong(cursor.getColumnIndexOrThrow("thingId")),
            locationId = cursor.getLong(cursor.getColumnIndexOrThrow("locationId")),
            linkedAt = cursor.getLong(cursor.getColumnIndexOrThrow("linkedAt")),
            active = cursor.getInt(cursor.getColumnIndexOrThrow("active")) == 1,
            serverId = cursor.getLong(cursor.getColumnIndexOrThrow("serverId")).takeIf { it != 0 }
        )
    }

    fun getLocationsForThing(thingId: Long): Flow<List<Location>> = callbackFlow {
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.rawQuery("""
                SELECT l.* FROM locations l
                INNER JOIN thing_location_links tll ON l.id = tll.locationId
                WHERE tll.thingId = ? AND tll.active = 1
                ORDER BY l.name ASC
            """.trimIndent(), arrayOf(thingId.toString()))
            try {
                val list = mutableListOf<Location>()
                while (cursor.moveToNext()) {
                    list.add(LocationDao(dbHelper).cursorToLocation(cursor))
                }
                trySend(list)
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        awaitClose()
    }

    fun getThingsForLocation(locationId: Long): Flow<List<Thing>> = callbackFlow {
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.rawQuery("""
                SELECT t.* FROM things t
                INNER JOIN thing_location_links tll ON t.id = tll.thingId
                WHERE tll.locationId = ? AND tll.active = 1
                ORDER BY t.name ASC
            """.trimIndent(), arrayOf(locationId.toString()))
            try {
                val list = mutableListOf<Thing>()
                while (cursor.moveToNext()) {
                    list.add(ThingDao(dbHelper).cursorToThing(cursor))
                }
                trySend(list)
            } finally {
                cursor.close()
            }
        } finally {
            db.close()
        }
        awaitClose()
    }

    suspend fun getLink(thingId: Long, locationId: Long): ThingLocationLink? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM thing_location_links WHERE thingId = ? AND locationId = ?",
            arrayOf(thingId.toString(), locationId.toString())
        )
        try {
            if (cursor.moveToFirst()) cursorToLink(cursor) else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun insert(link: ThingLocationLink): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("thingId", link.thingId)
            put("locationId", link.locationId)
            put("linkedAt", link.linkedAt)
            put("active", if (link.active) 1 else 0)
            link.serverId?.let { put("serverId", it) }
        }
        db.insertWithOnConflict("thing_location_links", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    suspend fun update(link: ThingLocationLink) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("linkedAt", link.linkedAt)
            put("active", if (link.active) 1 else 0)
            link.serverId?.let { put("serverId", it) }
        }
        db.update("thing_location_links", values, "thingId = ? AND locationId = ?", arrayOf(link.thingId.toString(), link.locationId.toString()))
        db.close()
    }

    suspend fun deactivate(thingId: Long, locationId: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("active", 0)
        }
        db.update("thing_location_links", values, "thingId = ? AND locationId = ?", arrayOf(thingId.toString(), locationId.toString()))
        db.close()
    }

    suspend fun getModifiedSince(since: Long): List<ThingLocationLink> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM thing_location_links WHERE linkedAt > ? ORDER BY linkedAt ASC",
            arrayOf(since.toString())
        )
        try {
            val list = mutableListOf<ThingLocationLink>()
            while (cursor.moveToNext()) {
                list.add(cursorToLink(cursor))
            }
            list
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun getMaxUpdatedAt(): Long? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(linkedAt) FROM thing_location_links", null)
        try {
            if (cursor.moveToFirst()) {
                val value = cursor.getLong(0)
                if (value > 0) value else null
            } else null
        } finally {
            cursor.close()
            db.close()
        }
    }
}