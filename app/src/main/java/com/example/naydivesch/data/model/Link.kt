package com.example.naydivesch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "links")
data class Link(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "thing_id")
    val thingId: Long,

    @ColumnInfo(name = "location_id")
    val locationId: Long,

    @ColumnInfo(name = "link_type")
    val linkType: String = "current",

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)

data class LinkRequest(
    @SerializedName("thing_id")
    val thingId: Long,

    @SerializedName("location_id")
    val locationId: Long,

    @SerializedName("link_type")
    val linkType: String = "current",

    @SerializedName("notes")
    val notes: String? = null
)

data class LinkResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("thing_id")
    val thingId: Long,

    @SerializedName("location_id")
    val locationId: Long,

    @SerializedName("link_type")
    val linkType: String = "current",

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("created_at")
    val createdAt: String = ""
)

fun LinkResponse.toEntity(): Link = Link(
    id = id,
    thingId = thingId,
    locationId = locationId,
    linkType = linkType,
    notes = notes,
    createdAt = createdAt,
    synced = true
)

fun Link.toRequest(): LinkRequest = LinkRequest(
    thingId = thingId,
    locationId = locationId,
    linkType = linkType,
    notes = notes
)