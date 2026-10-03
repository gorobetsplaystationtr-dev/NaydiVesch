package com.example.naydivesch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "things")
data class Thing(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "location_id")
    val locationId: Long? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "active",

    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",

    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

data class ThingRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("location_id")
    val locationId: Long? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("status")
    val status: String = "active"
)

data class ThingResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("location_id")
    val locationId: Long? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("status")
    val status: String = "active",

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)

fun ThingResponse.toEntity(): Thing = Thing(
    id = id,
    name = name,
    description = description,
    locationId = locationId,
    category = category,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt,
    synced = true
)

fun Thing.toRequest(): ThingRequest = ThingRequest(
    name = name,
    description = description,
    locationId = locationId,
    category = category,
    status = status
)