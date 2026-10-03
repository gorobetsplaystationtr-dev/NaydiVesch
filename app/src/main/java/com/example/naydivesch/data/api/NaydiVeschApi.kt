package com.example.naydivesch.data.api

import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NaydiVeschApi {

    // --- Получение списка ---

    @GET("things")
    suspend fun getThings(
        @Query("updated_since") updatedSince: Long? = null
    ): List<Thing>

    @GET("locations")
    suspend fun getLocations(
        @Query("updated_since") updatedSince: Long? = null
    ): List<Location>

    @GET("links")
    suspend fun getLinks(
        @Query("updated_since") updatedSince: Long? = null
    ): List<LinkResponse>

    // --- Thing операции ---

    @POST("things")
    suspend fun createThing(@Body thing: Thing): Thing

    @PUT("things/{id}")
    suspend fun updateThing(@Path("id") id: Long, @Body thing: Thing): Thing

    @DELETE("things/{id}")
    suspend fun deleteThing(@Path("id") id: Long)

    // --- Location операции ---

    @POST("locations")
    suspend fun createLocation(@Body location: Location): Location

    @PUT("locations/{id}")
    suspend fun updateLocation(@Path("id") id: Long, @Body location: Location): Location

    @DELETE("locations/{id}")
    suspend fun deleteLocation(@Path("id") id: Long)

    // --- Link операции ---

    @POST("links")
    suspend fun createLink(@Body request: LinkRequest): LinkResponse

    @DELETE("links")
    suspend fun deleteLink(
        @Query("thing_id") thingId: Long,
        @Query("location_id") locationId: Long
    )
}

data class LinkRequest(
    val thingId: Long,
    val locationId: Long
)

data class LinkResponse(
    val id: Long,
    val thingId: Long,
    val locationId: Long,
    val linkedAt: Long,
    val active: Boolean
)