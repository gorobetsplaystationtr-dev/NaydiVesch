package com.example.naydivesch.data.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.naydivesch.data.db.AppDatabaseHelper
import com.example.naydivesch.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class LocationDao(private val dbHelper: AppDatabaseHelper) {

    fun cursorToLocation(cursor: Cursor): Location {
        return Location(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            nfcUid = cursor.getString(cursor.getColumnIndexOrThrow("nfcUid")),
            qrCodeUrl = cursor.getString(cursor.getColumnIndexOrThrow("qrCodeUrl")),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")),
            serverId = cursor.getLong(cursor.getColumnIndexOrThrow("serverId")).takeIf { it != 0L }
        )
    }

    fun getAll(): Flow<List<Location>> = callbackFlow {
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.rawQuery("SELECT * FROM locations ORDER BY name ASC", null)
            try {
                val list = mutableListOf<Location>()
                while (cursor.moveToNext()) {
                    list.add(cursorToLocation(cursor))
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

    suspend fun getById(id: Long): Location? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM locations WHERE id = ?", arrayOf(id.toString()))
        try {
            if (cursor.moveToFirst()) cursorToLocation(cursor) else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun getByServerId(serverId: Long): Location? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM locations WHERE serverId = ?", arrayOf(serverId.toString()))
        try {
            if (cursor.moveToFirst()) cursorToLocation(cursor) else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun insert(location: Location): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", location.name)
            put("description", location.description)
            put("nfcUid", location.nfcUid)
            put("qrCodeUrl", location.qrCodeUrl)
            put("createdAt", location.createdAt)
            put("updatedAt", location.updatedAt)
            location.serverId?.let { put("serverId", it) }
        }
        try {
            db.insertOrThrow("locations", null, values)
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            db.update("locations", values, "name = ?", arrayOf(location.name))
            0
        } finally {
            db.close()
        }
    }

    suspend fun insertAll(locations: List<Location>) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            locations.forEach { insert(it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun update(location: Location) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", location.name)
            put("description", location.description)
            put("nfcUid", location.nfcUid)
            put("qrCodeUrl", location.qrCodeUrl)
            put("updatedAt", System.currentTimeMillis())
            location.serverId?.let { put("serverId", it) }
        }
        db.update("locations", values, "id = ?", arrayOf(location.id.toString()))
        db.close()
    }

    suspend fun delete(location: Location) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("locations", "id = ?", arrayOf(location.id.toString()))
        db.close()
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("locations", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    suspend fun getModifiedSince(since: Long): List<Location> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM locations WHERE updatedAt > ? ORDER BY updatedAt ASC",
            arrayOf(since.toString())
        )
        try {
            val list = mutableListOf<Location>()
            while (cursor.moveToNext()) {
                list.add(cursorToLocation(cursor))
            }
            list
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun getMaxUpdatedAt(): Long? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(updatedAt) FROM locations", null)
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