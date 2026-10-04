package com.example.naydivesch.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.naydivesch.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Long): Location?

    @Query("SELECT * FROM locations WHERE serverId = :serverId")
    suspend fun getByServerId(serverId: Long): Location?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<Location>)

    @Update
    suspend fun update(location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM locations WHERE updatedAt > :since ORDER BY updatedAt ASC")
    suspend fun getModifiedSince(since: Long): List<Location>

    @Query("SELECT MAX(updatedAt) FROM locations")
    suspend fun getMaxUpdatedAt(): Long?
}