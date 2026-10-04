package com.example.naydivesch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.example.naydivesch.model.ThingLocationLink
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface ThingLocationLinkDao {
    @Query("""
        SELECT l.* FROM locations l
        INNER JOIN thing_location_links tll ON l.id = tll.locationId
        WHERE tll.thingId = :thingId AND tll.active = 1
        ORDER BY l.name ASC
    """)
    fun getLocationsForThing(thingId: Long): Flow<List<Location>>

    @Query("""
        SELECT t.* FROM things t
        INNER JOIN thing_location_links tll ON t.id = tll.thingId
        WHERE tll.locationId = :locationId AND tll.active = 1
        ORDER BY t.name ASC
    """)
    fun getThingsForLocation(locationId: Long): Flow<List<Thing>>

    @Query("SELECT * FROM thing_location_links WHERE thingId = :thingId AND locationId = :locationId")
    suspend fun getLink(thingId: Long, locationId: Long): ThingLocationLink?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: ThingLocationLink): Long

    @Update
    suspend fun update(link: ThingLocationLink)

    @Query("UPDATE thing_location_links SET active = 0 WHERE thingId = :thingId AND locationId = :locationId")
    suspend fun deactivate(thingId: Long, locationId: Long)

    @Query("SELECT * FROM thing_location_links WHERE linkedAt > :since ORDER BY linkedAt ASC")
    suspend fun getModifiedSince(since: Date): List<ThingLocationLink>

    @Query("SELECT MAX(linkedAt) FROM thing_location_links")
    suspend fun getMaxUpdatedAt(): Date?
}