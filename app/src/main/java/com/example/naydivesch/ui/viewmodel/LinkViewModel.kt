package com.example.naydivesch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.naydivesch.data.repository.NaydiVeschRepository
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LinkViewModel(
    private val repository: NaydiVeschRepository,
    private val thingId: Long,
    private val locationId: Long
) : ViewModel() {

    val thing: StateFlow<Thing?> =
        repository.getThing(thingId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val location: StateFlow<Location?> =
        repository.getLocation(locationId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _linkResult = MutableStateFlow<LinkResult>(LinkResult.Idle)
    val linkResult: StateFlow<LinkResult> = _linkResult

    val locationsForThing: StateFlow<List<Location>> =
        repository.getLocationsForThing(thingId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val thingsForLocation: StateFlow<List<Thing>> =
        repository.getThingsForLocation(locationId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allThings: StateFlow<List<Thing>> =
        repository.getAllThings()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allLocations: StateFlow<List<Location>> =
        repository.getAllLocations()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun link() {
        viewModelScope.launch {
            try {
                repository.linkThingToLocation(thingId, locationId)
                _linkResult.value = LinkResult.Success
            } catch (e: Exception) {
                _linkResult.value = LinkResult.Error(e.message ?: "Ошибка привязки")
            }
        }
    }

    fun unlink() {
        viewModelScope.launch {
            try {
                repository.unlinkThingFromLocation(thingId, locationId)
                _linkResult.value = LinkResult.Unlinked
            } catch (e: Exception) {
                _linkResult.value = LinkResult.Error(e.message ?: "Ошибка отвязки")
            }
        }
    }

    sealed interface LinkResult {
        object Idle : LinkResult
        object Success : LinkResult
        object Unlinked : LinkResult
        data class Error(val message: String) : LinkResult
    }
}

class LinkViewModelFactory(
    private val repository: NaydiVeschRepository,
    private val thingId: Long,
    private val locationId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LinkViewModel(repository, thingId, locationId) as T
    }
}
