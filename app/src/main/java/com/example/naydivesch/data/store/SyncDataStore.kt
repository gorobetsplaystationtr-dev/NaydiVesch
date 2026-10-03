package com.example.naydivesch.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_preferences")

class SyncDataStore(private val context: Context) {

    companion object {
        val SYNC_THINGS_KEY = booleanPreferencesKey("sync_things")
        val SYNC_LOCATIONS_KEY = booleanPreferencesKey("sync_locations")
        val LAST_SYNC_TIME_KEY = longPreferencesKey("last_sync_time")
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
        val IS_INITIALIZED_KEY = booleanPreferencesKey("is_initialized")
    }

    fun getIsInitialized(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_INITIALIZED_KEY] ?: false
        }
    }

    fun getLastSyncTime(): Flow<Long> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_SYNC_TIME_KEY] ?: 0L
        }
    }

    fun getServerUrl(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[SERVER_URL_KEY] ?: "http://192.168.2.12:8080/api/"
        }
    }

    suspend fun setInitialized(isInitialized: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_INITIALIZED_KEY] = isInitialized
        }
    }

    suspend fun setLastSyncTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME_KEY] = timestamp
        }
    }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL_KEY] = url
        }
    }
}