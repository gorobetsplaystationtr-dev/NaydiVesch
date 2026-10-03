package com.example.naydivesch.data.api

import com.example.naydivesch.data.model.HistoryResponse
import com.example.naydivesch.data.model.LinkRequest
import com.example.naydivesch.data.model.LinkResponse
import com.example.naydivesch.data.model.LocationRequest
import com.example.naydivesch.data.model.LocationResponse
import com.example.naydivesch.data.model.ThingRequest
import com.example.naydivesch.data.model.ThingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ==================== THINGS ====================

    @GET("things")
    suspend fun getThings(
        @Query("location_id") locationId: Long? = null,
        @Query("status") status: String? = null
    ): Response<List<ThingResponse>>

    @GET("things/{id}")
    suspend fun getThingById(
        @Path("id") id: Long
    ): Response<ThingResponse>

    @POST("things")
    suspend fun createThing(
        @Body request: ThingRequest
    ): Response<ThingResponse>

    @PUT("things/{id}")
    suspend fun updateThing(
        @Path("id") id: Long,
        @Body request: ThingRequest
    ): Response<ThingResponse>

    @DELETE("things/{id}")
    suspend fun deleteThing(
        @Path("id") id: Long
    ): Response<Unit>

    // ==================== LOCATIONS ====================

    @GET("locations")
    suspend fun getLocations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<LocationResponse>>

    @GET("locations/{id}")
    suspend fun getLocationById(
        @Path("id") id: Long
    ): Response<LocationResponse>

    @POST("locations")
    suspend fun createLocation(
        @Body request: LocationRequest
    ): Response<LocationResponse>

    @PUT("locations/{id}")
    suspend fun updateLocation(
        @Path("id") id: Long,
        @Body request: LocationRequest
    ): Response<LocationResponse>

    @DELETE("locations/{id}")
    suspend fun deleteLocation(
        @Path("id") id: Long
    ): Response<Unit>

    // ==================== LINKS ====================

    @GET("links")
    suspend fun getLinks(
        @Query("thing_id") thingId: Long? = null,
        @Query("location_id") locationId: Long? = null
    ): Response<List<LinkResponse>>

    @POST("links")
    suspend fun createLink(
        @Body request: LinkRequest
    ): Response<LinkResponse>

    // ==================== HISTORY ====================

    @GET("history")
    suspend fun getHistory(
        @Query("thing_id") thingId: Long? = null,
        @Query("location_id") locationId: Long? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("limit") limit: Int = 100
    ): Response<List<HistoryResponse>>
}