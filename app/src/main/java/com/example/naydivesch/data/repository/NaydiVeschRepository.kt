package com.example.naydivesch.data.network

import android.content.Context
import android.content.SharedPreferences
import com.example.naydivesch.data.api.LinkRequest
import com.example.naydivesch.data.api.NaydiVeschApi
import com.example.naydivesch.data.dao.LocationDao
import com.example.naydivesch.data.dao.ThingDao
import com.example.naydivesch.data.dao.ThingLocationLinkDao
import com.example.naydivesch.data.db.AppDatabaseHelper
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.example.naydivesch.model.ThingLocationLink
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NaydiVeschRepository private constructor(
    private val api: NaydiVeschApi,
    private val thingDao: ThingDao,
    private val locationDao: LocationDao,
    private val linkDao: ThingLocationLinkDao,
    private val prefs: SharedPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // === Локальные потоки данных (SQLite) ===

    fun getAllThings(): Flow<List<Thing>> = thingDao.getAll()

    fun getAllLocations(): Flow<List<Location>> = locationDao.getAll()

    fun getLocationsForThing(thingId: Long): Flow<List<Location>> =
        linkDao.getLocationsForThing(thingId)

    fun getThingsForLocation(locationId: Long): Flow<List<Thing>> =
        linkDao.getThingsForLocation(locationId)

    // === Thing CRUD ===

    suspend fun getThing(id: Long): Thing? =
        withContext(ioDispatcher) { thingDao.getById(id) }

    suspend fun addThing(thing: Thing): Long = withContext(ioDispatcher) {
        val localId = thingDao.insert(thing)
        trySyncThingCreate(thing.copy(id = localId))
        localId
    }

    suspend fun updateThing(thing: Thing) = withContext(ioDispatcher) {
        thingDao.update(thing)
        trySyncThingUpdate(thing)
    }

    suspend fun deleteThing(thing: Thing) = withContext(ioDispatcher) {
        val serverId = thing.serverId
        thingDao.delete(thing)
        if (serverId != null) {
            try { api.deleteThing(serverId) } catch (e: Exception) { }
        }
    }

    // === Location CRUD ===

    suspend fun getLocation(id: Long): Location? =
        withContext(ioDispatcher) { locationDao.getById(id) }

    suspend fun addLocation(location: Location): Long = withContext(ioDispatcher) {
        val localId = locationDao.insert(location)
        trySyncLocationCreate(location.copy(id = localId))
        localId
    }

    suspend fun updateLocation(location: Location) = withContext(ioDispatcher) {
        locationDao.update(location)
        trySyncLocationUpdate(location)
    }

    suspend fun deleteLocation(location: Location) = withContext(ioDispatcher) {
        val serverId = location.serverId
        locationDao.delete(location)
        if (serverId != null) {
            try { api.deleteLocation(serverId) } catch (e: Exception) { }
        }
    }

    // === Link CRUD ===

    suspend fun linkThingToLocation(thingId: Long, locationId: Long): Long =
        withContext(ioDispatcher) {
            val link = ThingLocationLink(thingId = thingId, locationId = locationId)
            val localId = linkDao.insert(link)
            trySyncLinkCreate(link)
            localId
        }

    suspend fun unlinkThingFromLocation(thingId: Long, locationId: Long) =
        withContext(ioDispatcher) {
            linkDao.deactivate(thingId, locationId)
            try { api.deleteLink(thingId, locationId) } catch (e: Exception) { }
        }

    // === Синхронизация с сервером ===

    suspend fun syncWithServer() = withContext(ioDispatcher) {
        val lastSync = getLastSyncTimestamp()
        val lastTimestamp = lastSync

        // 1. Сервер → локальная БД
        pullFromServer(lastTimestamp)

        // 2. Локальные изменения → сервер
        pushLocalChanges(lastTimestamp)

        // 3. Запомнить время синхронизации
        setLastSyncTimestamp(System.currentTimeMillis())
    }

    private suspend fun pullFromServer(since: Long) {
        // Things
        try {
            api.getThings(updatedSince = since)?.forEach { serverThing ->
                val local = thingDao.getByServerId(serverThing.id)
                if (local == null || serverThing.updatedAt > (local.updatedAt)) {
                    thingDao.insert(
                        serverThing.copy(id = local?.id ?: 0)
                    )
                }
            }
        } catch (e: Exception) { }

        // Locations
        try {
            api.getLocations(updatedSince = since)?.forEach { serverLocation ->
                val local = locationDao.getByServerId(serverLocation.id)
                if (local == null || serverLocation.updatedAt > (local.updatedAt)) {
                    locationDao.insert(
                        serverLocation.copy(id = local?.id ?: 0)
                    )
                }
            }
        } catch (e: Exception) { }

        // Links
        try {
            api.getLinks(updatedSince = since)?.forEach { linkResponse ->
                val existing = linkDao.getLink(linkResponse.thingId, linkResponse.locationId)
                if (existing == null) {
                    linkDao.insert(
                        ThingLocationLink(
                                thingId = linkResponse.thingId,
                                locationId = linkResponse.locationId,
                                linkedAt = linkResponse.linkedAt,
                                active = linkResponse.active,
                                serverId = linkResponse.id
                        )
                    )
                }
            }
        } catch (e: Exception) { }
    }

    private suspend fun pushLocalChanges(since: Long) {
        thingDao.getModifiedSince(since).forEach { trySyncThingCreate(it); trySyncThingUpdate(it) }
        locationDao.getModifiedSince(since).forEach { trySyncLocationCreate(it); trySyncLocationUpdate(it) }
        linkDao.getModifiedSince(since).forEach { trySyncLinkCreate(it) }
    }

    private suspend fun trySyncThingCreate(thing: Thing) {
        if (thing.serverId != null) return
        try {
            val serverThing = api.createThing(thing)
            thingDao.update(thing.copy(serverId = serverThing.serverId ?: serverThing.id))
        } catch (e: Exception) { }
    }

    private suspend fun trySyncThingUpdate(thing: Thing) {
        val serverId = thing.serverId ?: return
        try { api.updateThing(serverId, thing) } catch (e: Exception) { }
    }

    private suspend fun trySyncLocationCreate(location: Location) {
        if (location.serverId != null) return
        try {
            val serverLocation = api.createLocation(location)
            locationDao.update(location.copy(serverId = serverLocation.serverId ?: serverLocation.id))
        } catch (e: Exception) { }
    }

    private suspend fun trySyncLocationUpdate(location: Location) {
        val serverId = location.serverId ?: return
        try { api.updateLocation(serverId, location) } catch (e: Exception) { }
    }

    private suspend fun trySyncLinkCreate(link: ThingLocationLink) {
        if (link.serverId != null) return
        try {
            val response = api.createLink(LinkRequest(link.thingId, link.locationId))
            linkDao.update(link.copy(serverId = response.id))
        } catch (e: Exception) { }
    }

    // === SharedPreferences для sync timestamp ===

    private fun getLastSyncTimestamp(): Long =
        prefs.getLong(KEY_LAST_SYNC, 0L)

    private fun setLastSyncTimestamp(ts: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, ts).apply()
    }

    companion object {
        private const val KEY_LAST_SYNC = "last_sync_timestamp"

        @Volatile
        @JvmStatic
        private var INSTANCE: NaydiVeschRepository? = null

        fun getInstance(
            context: Context,
            api: NaydiVeschApi
        ): NaydiVeschRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val dbHelper = AppDatabaseHelper.getInstance(context)
                    val prefs = context.getSharedPreferences("naydivesch_prefs", Context.MODE_PRIVATE)
                    val repo = NaydiVeschRepository(
                        api = api,
                        thingDao = ThingDao(dbHelper),
                        locationDao = LocationDao(dbHelper),
                        linkDao = ThingLocationLinkDao(dbHelper),
                        prefs = prefs
                    )
                    INSTANCE = repo
                    repo
                }
            }
        }
    }
}