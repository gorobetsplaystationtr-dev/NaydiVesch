package com.example.naydivesch.data.repository

import com.example.naydivesch.data.api.ApiClient
import com.example.naydivesch.data.database.HistoryDao
import com.example.naydivesch.data.database.LinkDao
import com.example.naydivesch.data.database.LocationDao
import com.example.naydivesch.data.database.ThingDao
import com.example.naydivesch.data.model.History
import com.example.naydivesch.data.model.HistoryResponse
import com.example.naydivesch.data.model.Link
import com.example.naydivesch.data.model.LinkRequest
import com.example.naydivesch.data.model.LinkResponse
import com.example.naydivesch.data.model.Location
import com.example.naydivesch.data.model.LocationRequest
import com.example.naydivesch.data.model.LocationResponse
import com.example.naydivesch.data.model.Thing
import com.example.naydivesch.data.model.ThingRequest
import com.example.naydivesch.data.model.ThingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

class ItemRepository(
    private val thingDao: ThingDao,
    private val locationDao: LocationDao,
    private val linkDao: LinkDao,
    private val historyDao: HistoryDao
) {

    // ==================== THINGS ====================

    fun getAllThings(): Flow<List<Thing>> = thingDao.getAllThings()

    suspend fun getThingById(id: Long): Thing? {
        return thingDao.getThingById(id) ?: try {
            val response = ApiClient.apiService.getThingById(id)
            if (response.isSuccessful) response.body()?.toEntity()?.also { thingDao.insertThing(it) } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createThing(request: ThingRequest): Result<Thing> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.createThing(request)
            if (response.isSuccessful) {
                val body = response.body()!!
                val entity = body.toEntity()
                thingDao.insertThing(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateThing(id: Long, request: ThingRequest): Result<Thing> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.updateThing(id, request)
            if (response.isSuccessful) {
                val body = response.body()!!
                val entity = body.toEntity()
                thingDao.insertThing(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteThing(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ApiClient.apiService.deleteThing(id)
            thingDao.getThingById(id)?.let { thingDao.deleteThing(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncThings() = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.getThings()
            if (response.isSuccessful) {
                val things = response.body() ?: emptyList()
                things.forEach { thingDao.insertThing(it.toEntity()) }
                thingDao.markAllSynced()
                Result.success(things.size)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== LOCATIONS ====================

    fun getAllLocations(): Flow<List<Location>> = locationDao.getAllLocations()

    suspend fun getLocationById(id: Long): Location? {
        return locationDao.getLocationById(id) ?: try {
            val response = ApiClient.apiService.getLocationById(id)
            if (response.isSuccessful) response.body()?.toEntity()?.also { locationDao.insertLocation(it) } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createLocation(request: LocationRequest): Result<Location> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.createLocation(request)
            if (response.isSuccessful) {
                val body = response.body()!!
                val entity = body.toEntity()
                locationDao.insertLocation(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocation(id: Long, request: LocationRequest): Result<Location> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.updateLocation(id, request)
            if (response.isSuccessful) {
                val body = response.body()!!
                val entity = body.toEntity()
                locationDao.insertLocation(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLocation(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ApiClient.apiService.deleteLocation(id)
            locationDao.getLocationById(id)?.let { locationDao.deleteLocation(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncLocations() = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.getLocations()
            if (response.isSuccessful) {
                val locations = response.body() ?: emptyList()
                locations.forEach { locationDao.insertLocation(it.toEntity()) }
                locationDao.markAllSynced()
                Result.success(locations.size)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== LINKS ====================

    fun getAllLinks(): Flow<List<Link>> = linkDao.getAllLinks()

    fun getLinksByThing(thingId: Long): Flow<List<Link>> = linkDao.getLinksByThing(thingId)

    fun getLinksByLocation(locationId: Long): Flow<List<Link>> = linkDao.getLinksByLocation(locationId)

    suspend fun createLink(request: LinkRequest): Result<Link> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.createLink(request)
            if (response.isSuccessful) {
                val body = response.body()!!
                val entity = body.toEntity()
                linkDao.insertLink(entity)
                Result.success(entity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncLinks() = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.getLinks()
            if (response.isSuccessful) {
                val links = response.body() ?: emptyList()
                links.forEach { linkDao.insertLink(it.toEntity()) }
                linkDao.markAllSynced()
                Result.success(links.size)
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HISTORY ====================

    fun getAllHistory(): Flow<List<History>> = historyDao.getAllHistory()

    suspend fun syncHistory(): Result<List<History>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.apiService.getHistory()
            if (response.isSuccessful) {
                val histories = response.body() ?: emptyList()
                histories.forEach { historyDao.insertHistory(it.toEntity()) }
                historyDao.markAllSynced()
                Result.success(histories.map { it.toEntity() })
            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}