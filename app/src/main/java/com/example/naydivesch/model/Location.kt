package com.example.naydivesch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.naydivesch.data.db.converters.DateConverter
import java.util.Date

@Entity(tableName = "locations")
@TypeConverters(DateConverter::class)
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val nfcUid: String? = null,
    val qrCodeUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val serverId: Long? = null
)