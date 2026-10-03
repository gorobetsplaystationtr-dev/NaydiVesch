package com.example.naydivesch.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.naydivesch.data.db.converters.DateConverter
import java.util.Date

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
@TypeConverters(DateConverter::class)
data class ThingLocationLink(
    val thingId: Long,
    val locationId: Long,
    val linkedAt: Date = Date(),
    val active: Boolean = true,
    val serverId: Long? = null
)