package com.example.naydivesch.data.database

import androidx.room.*
import com.example.naydivesch.data.model.Thing
import kotlinx.coroutines.flow.Flow

@Dao
interface ThingDao {
    @Query("SELECT * FROM things ORDER BY created_at DESC")
    fun getAllThings(): Flow<List<Thing>>

    @Query("SELECT * FROM things WHERE id = :id")
    suspend fun getThingById(id: Long): Thing?

    @Query("SELECT * FROM things WHERE location_id = :locationId")
    fun getThingsByLocation(locationId: Long): Flow<List<Thing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThing(thing: Thing): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThings(things: List<Thing>)

    @Update
    suspend fun updateThing(thing: Thing)

    @Delete
    suspend fun deleteThing(thing: Thing)

    @Query("DELETE FROM things")
    suspend fun deleteAllThings()

    @Query("UPDATE things SET synced = :synced WHERE id = :id")
    suspend fun markSynced(id: Long, synced: Boolean)

    @Query("UPDATE things SET synced = 1")
    suspend fun markAllSynced()
}