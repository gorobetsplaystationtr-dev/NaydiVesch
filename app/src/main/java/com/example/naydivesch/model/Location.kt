package com.example.naydivesch.model

data class Location(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val nfcUid: String? = null,
    val qrCodeUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: Long? = null
)