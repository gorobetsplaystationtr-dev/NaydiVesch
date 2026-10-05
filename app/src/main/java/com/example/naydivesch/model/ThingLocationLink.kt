package com.example.naydivesch.model

data class ThingLocationLink(
    val thingId: Long,
    val locationId: Long,
    val linkedAt: Long = System.currentTimeMillis(),
    val active: Boolean = true,
    val serverId: Long? = null
)