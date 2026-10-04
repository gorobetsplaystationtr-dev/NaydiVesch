package com.example.naydivesch.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "thing_location_links",
    primaryKeys = ["thingId", "locationId"],
    foreignKeys = [
        ForeignKey(
            entity = Thing::class,
            parentColumns = ["id"],
            childColumns = ["thingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ThingLocationLink(
    val thingId: Long,
    val locationId: Long,
    val linkedAt: Long = System.currentTimeMillis(),
    val active: Boolean = true,
    val serverId: Long? = null
)