package com.example.naydivesch.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.naydivesch.model.Thing
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface ThingDao {
    @Query("SELECT * FROM things ORDER BY name ASC")
    fun getAll(): Flow<List<Thing>>

    @Query("SELECT * FROM things WHERE id = :id")
    suspend fun getById(id: Long): Thing?

    @Query("SELECT * FROM things WHERE serverId = :serverId")
    suspend fun getByServerId(serverId: Long): Thing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(thing: Thing): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(things: List<Thing>)

    @Update
    suspend fun update(thing: Thing)

    @Delete
    suspend fun delete(thing: Thing)

    @Query("DELETE FROM things WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM things WHERE updatedAt > :since ORDER BY updatedAt ASC")
    suspend fun getModifiedSince(since: Date): List<Thing>

    @Query("SELECT MAX(updatedAt) FROM things")
    suspend fun getMaxUpdatedAt(): Date?
}