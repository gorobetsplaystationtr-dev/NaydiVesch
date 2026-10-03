package com.example.naydivesch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naydivesch.data.database.DatabaseProvider
import com.example.naydivesch.data.model.History
import com.example.naydivesch.data.model.Link
import com.example.naydivesch.data.model.Location
import com.example.naydivesch.data.model.Thing
import com.example.naydivesch.data.store.SettingsDataStore
import com.example.naydivesch.data.store.SyncDataStore
import com.example.naydivesch.data.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    context: android.content.Context
) : ViewModel() {

    private val syncDataStore = SyncDataStore(context)
    private val settingsDataStore = SettingsDataStore(context)

    private val repository = ItemRepository(
        thingDao = DatabaseProvider.getDatabase(context).thingDao(),
        locationDao = DatabaseProvider.getDatabase(context).locationDao(),
        linkDao = DatabaseProvider.getDatabase(context).linkDao(),
        historyDao = DatabaseProvider.getDatabase(context).historyDao()
    )

    // Things
    val allThings: Flow<List<Thing>> = repository.getAllThings()

    fun getThingById(id: Long) = viewModelScope.launch {
        repository.getThingById(id)
    }

    fun createThing(name: String, description: String?, locationId: Long?, category: String?) {
        viewModelScope.launch {
            repository.createThing(
                com.example.naydivesch.data.model.ThingRequest(
                    name = name,
                    description = description,
                    locationId = locationId,
                    category = category
                )
            )
        }
    }

    fun updateThing(id: Long, name: String, description: String?, locationId: Long?, category: String?) {
        viewModelScope.launch {
            repository.updateThing(
                id,
                com.example.naydivesch.data.model.ThingRequest(
                    name = name,
                    description = description,
                    locationId = locationId,
                    category = category
                )
            )
        }
    }

    fun deleteThing(id: Long) = viewModelScope.launch {
        repository.deleteThing(id)
    }

    fun syncThings() = viewModelScope.launch {
        repository.syncThings()
    }

    // Locations
    val allLocations: Flow<List<Location>> = repository.getAllLocations()

    fun getLocationById(id: Long) = viewModelScope.launch {
        repository.getLocationById(id)
    }

    fun createLocation(name: String, address: String?, latitude: Double?, longitude: Double?, description: String?) {
        viewModelScope.launch {
            repository.createLocation(
                com.example.naydivesch.data.model.LocationRequest(
                    name = name,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    description = description
                )
            )
        }
    }

    fun updateLocation(id: Long, name: String, address: String?, latitude: Double?, longitude: Double?, description: String?) {
        viewModelScope.launch {
            repository.updateLocation(
                id,
                com.example.naydivesch.data.model.LocationRequest(
                    name = name,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    description = description
                )
            )
        }
    }

    fun deleteLocation(id: Long) = viewModelScope.launch {
        repository.deleteLocation(id)
    }

    fun syncLocations() = viewModelScope.launch {
        repository.syncLocations()
    }

    // Links
    val allLinks: Flow<List<Link>> = repository.getAllLinks()

    fun getLinksByThing(thingId: Long) = repository.getLinksByThing(thingId)

    fun getLinksByLocation(locationId: Long) = repository.getLinksByLocation(locationId)

    fun createLink(thingId: Long, locationId: Long, linkType: String = "current", notes: String? = null) {
        viewModelScope.launch {
            repository.createLink(
                com.example.naydivesch.data.model.LinkRequest(
                    thingId = thingId,
                    locationId = locationId,
                    linkType = linkType,
                    notes = notes
                )
            )
        }
    }

    fun syncLinks() = viewModelScope.launch {
        repository.syncLinks()
    }

    // History
    val allHistory: Flow<List<History>> = repository.getAllHistory()

    fun syncHistory() = viewModelScope.launch {
        repository.syncHistory()
    }

    // DataStore
    val autoSync: Boolean
        get() = settingsDataStore.getAutoSync()

    fun setAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAutoSync(enabled)
        }
    }
}