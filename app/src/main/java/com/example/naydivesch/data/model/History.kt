package com.example.naydivesch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "thing_id")
    val thingId: Long? = null,

    @ColumnInfo(name = "location_id")
    val locationId: Long? = null,

    @ColumnInfo(name = "action")
    val action: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "timestamp")
    val timestamp: String = "",

    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

data class HistoryResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("thing_id")
    val thingId: Long? = null,

    @SerializedName("location_id")
    val locationId: Long? = null,

    @SerializedName("action")
    val action: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("timestamp")
    val timestamp: String = ""
)

fun HistoryResponse.toEntity(): History = History(
    id = id,
    thingId = thingId,
    locationId = locationId,
    action = action,
    description = description,
    timestamp = timestamp,
    synced = true
)