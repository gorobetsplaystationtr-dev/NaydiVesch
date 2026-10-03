package com.example.naydivesch.data.database

import androidx.room.*
import com.example.naydivesch.data.model.Link
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    @Query("SELECT * FROM links ORDER BY created_at DESC")
    fun getAllLinks(): Flow<List<Link>>

    @Query("SELECT * FROM links WHERE id = :id")
    suspend fun getLinkById(id: Long): Link?

    @Query("SELECT * FROM links WHERE thing_id = :thingId")
    fun getLinksByThing(thingId: Long): Flow<List<Link>>

    @Query("SELECT * FROM links WHERE location_id = :locationId")
    fun getLinksByLocation(locationId: Long): Flow<List<Link>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: Link): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<Link>)

    @Delete
    suspend fun deleteLink(link: Link)

    @Query("DELETE FROM links")
    suspend fun deleteAllLinks()

    @Query("UPDATE links SET synced = 1")
    suspend fun markAllSynced()
}