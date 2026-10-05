package com.example.naydivesch.model

data class Thing(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val serverId: Long? = null
)