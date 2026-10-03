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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Универсальная ViewModel для экрана привязки, где пользователь выбирает
 * конкретную вещь и место из списков.
 */
class SelectLinkViewModel(
    private val repository: NaydiVeschRepository
) : ViewModel() {

    val allThings: StateFlow<List<Thing>> =
        repository.getAllThings()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allLocations: StateFlow<List<Location>> =
        repository.getAllLocations()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedThing = MutableStateFlow<Thing?>(null)
    val selectedLocation = MutableStateFlow<Location?>(null)

    val canLink: StateFlow<Boolean> = combine(
        selectedThing, selectedLocation
    ) { t, l -> t != null && l != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun linkSelected() {
        val t = selectedThing.value
        val l = selectedLocation.value
        if (t != null && l != null) {
            viewModelScope.launch {
                repository.linkThingToLocation(t.id, l.id)
            }
        }
    }
}

class SelectLinkViewModelFactory(
    private val repository: NaydiVeschRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectLinkViewModel(repository) as T
    }
}
