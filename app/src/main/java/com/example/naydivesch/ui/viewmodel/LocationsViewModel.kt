package com.example.naydivesch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.naydivesch.data.repository.NaydiVeschRepository
import com.example.naydivesch.model.Location
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocationsViewModel(
    private val repository: NaydiVeschRepository
) : ViewModel() {

    val locations: StateFlow<List<Location>> =
        repository.getAllLocations()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addLocation(name: String, description: String, nfcUid: String?, qrCodeUrl: String?) {
        viewModelScope.launch {
            repository.addLocation(
                Location(
                    name = name,
                    description = description,
                    nfcUid = nfcUid,
                    qrCodeUrl = qrCodeUrl
                )
            )
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncWithServer()
        }
    }
}

class LocationsViewModelFactory(
    private val repository: NaydiVeschRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationsViewModel(repository) as T
    }
}
