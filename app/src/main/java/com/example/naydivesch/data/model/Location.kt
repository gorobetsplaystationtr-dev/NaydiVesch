package com.example.naydivesch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String? = null,

    @ColumnInfo(name = "latitude")
    val latitude: Double? = null,

    @ColumnInfo(name = "longitude")
    val longitude: Double? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "nfc_tag_id")
    val nfcTagId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",

    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

data class LocationRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("description")
    val description: String? = null
)

data class LocationResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("nfc_tag_id")
    val nfcTagId: String? = null,

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)

fun LocationResponse.toEntity(): Location = Location(
    id = id,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    description = description,
    nfcTagId = nfcTagId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    synced = true
)

fun Location.toRequest(): LocationRequest = LocationRequest(
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    description = description
)