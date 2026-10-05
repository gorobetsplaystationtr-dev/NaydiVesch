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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val AUTO_SYNC_KEY = booleanPreferencesKey("auto_sync")
        val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
    }

    suspend fun isAutoSyncEnabled(): Boolean {
        return try {
            context.settingsDataStore.data.first()[AUTO_SYNC_KEY] ?: true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun isNotificationEnabled(): Boolean {
        return try {
            context.settingsDataStore.data.first()[NOTIFICATION_ENABLED_KEY] ?: true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun setAutoSync(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[AUTO_SYNC_KEY] = enabled
        }
    }
}