package com.example.naydivesch.data.database

import androidx.room.*
import com.example.naydivesch.data.model.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<History>>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Long): History?

    @Query("SELECT * FROM history WHERE thing_id = :thingId ORDER BY timestamp DESC")
    fun getHistoryByThing(thingId: Long): Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<History>)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()

    @Query("UPDATE history SET synced = 1")
    suspend fun markAllSynced()
}