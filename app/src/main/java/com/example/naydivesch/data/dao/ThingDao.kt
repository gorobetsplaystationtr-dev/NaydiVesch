package com.example.naydivesch.data.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.naydivesch.data.db.AppDatabaseHelper
import com.example.naydivesch.model.Thing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class ThingDao(private val dbHelper: AppDatabaseHelper) {

    private fun cursorToThing(cursor: Cursor): Thing {
        return Thing(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
            photoUrl = cursor.getString(cursor.getColumnIndexOrThrow("photoUrl")),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")),
            serverId = cursor.getLong(cursor.getColumnIndexOrThrow("serverId")).takeIf { it != 0 }
        )
    }

    fun getAll(): Flow<List<Thing>> = callbackFlow {
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.rawQuery("SELECT * FROM things ORDER BY name ASC", null)
            try {
                val list = mutableListOf<Thing>()
                while (cursor.moveToNext()) {
                    list.add(cursorToThing(cursor))
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

    suspend fun getById(id: Long): Thing? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM things WHERE id = ?", arrayOf(id.toString()))
        try {
            if (cursor.moveToFirst()) cursorToThing(cursor) else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun getByServerId(serverId: Long): Thing? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM things WHERE serverId = ?", arrayOf(serverId.toString()))
        try {
            if (cursor.moveToFirst()) cursorToThing(cursor) else null
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun insert(thing: Thing): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", thing.name)
            put("description", thing.description)
            put("category", thing.category)
            put("photoUrl", thing.photoUrl)
            put("createdAt", thing.createdAt)
            put("updatedAt", thing.updatedAt)
            thing.serverId?.let { put("serverId", it) }
        }
        try {
            db.insertOrThrow("things", null, values)
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            db.update("things", values, "name = ?", arrayOf(thing.name))
            0 // fallback
        } finally {
            db.close()
        }
    }

    suspend fun insertAll(things: List<Thing>) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            things.forEach { insert(it) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    suspend fun update(thing: Thing) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", thing.name)
            put("description", thing.description)
            put("category", thing.category)
            put("photoUrl", thing.photoUrl)
            put("updatedAt", System.currentTimeMillis())
            thing.serverId?.let { put("serverId", it) }
        }
        db.update("things", values, "id = ?", arrayOf(thing.id.toString()))
        db.close()
    }

    suspend fun delete(thing: Thing) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("things", "id = ?", arrayOf(thing.id.toString()))
        db.close()
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("things", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    suspend fun getModifiedSince(since: Long): List<Thing> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM things WHERE updatedAt > ? ORDER BY updatedAt ASC",
            arrayOf(since.toString())
        )
        try {
            val list = mutableListOf<Thing>()
            while (cursor.moveToNext()) {
                list.add(cursorToThing(cursor))
            }
            list
        } finally {
            cursor.close()
            db.close()
        }
    }

    suspend fun getMaxUpdatedAt(): Long? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(updatedAt) FROM things", null)
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