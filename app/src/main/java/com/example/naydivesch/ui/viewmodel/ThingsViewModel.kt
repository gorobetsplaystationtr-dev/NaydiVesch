package com.example.naydivesch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.naydivesch.data.repository.NaydiVeschRepository
import com.example.naydivesch.model.Thing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThingsViewModel(
    private val repository: NaydiVeschRepository
) : ViewModel() {

    val things: StateFlow<List<Thing>> =
        repository.getAllThings()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isLoading: StateFlow<Boolean> =
        things.map { it.isNotEmpty() }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun addThing(name: String, description: String, category: String, photoUrl: String?) {
        viewModelScope.launch {
            repository.addThing(
                Thing(
                    name = name,
                    description = description,
                    category = category,
                    photoUrl = photoUrl
                )
            )
        }
    }

    fun deleteThing(thing: Thing) {
        viewModelScope.launch {
            repository.deleteThing(thing)
        }
    }

    fun sync() {
        viewModelScope.launch {
            repository.syncWithServer()
        }
    }
}

class ThingsViewModelFactory(
    private val repository: NaydiVeschRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ThingsViewModel(repository) as T
    }
}
