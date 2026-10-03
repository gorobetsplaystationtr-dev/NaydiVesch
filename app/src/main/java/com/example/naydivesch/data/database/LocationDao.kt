package com.example.naydivesch.data.database

import androidx.room.*
import com.example.naydivesch.data.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY created_at DESC")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Long): Location?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<Location>)

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()

    @Query("UPDATE locations SET synced = :synced WHERE id = :id")
    suspend fun markSynced(id: Long, synced: Boolean)

    @Query("UPDATE locations SET synced = 1")
    suspend fun markAllSynced()
}