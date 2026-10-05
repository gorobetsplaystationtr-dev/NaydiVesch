package com.example.naydivesch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naydivesch.data.api.NaydiVeschApi
import com.example.naydivesch.data.repository.NaydiVeschRepository
import com.example.naydivesch.ui.screens.LinkScreen
import com.example.naydivesch.ui.screens.LocationsScreen
import com.example.naydivesch.ui.screens.ThingsScreen
import com.example.naydivesch.ui.viewmodel.LocationsViewModel
import com.example.naydivesch.ui.viewmodel.LocationsViewModelFactory
import com.example.naydivesch.ui.viewmodel.SelectLinkViewModel
import com.example.naydivesch.ui.viewmodel.SelectLinkViewModelFactory
import com.example.naydivesch.ui.viewmodel.ThingsViewModel
import com.example.naydivesch.ui.viewmodel.ThingsViewModelFactory
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NaydiVeschApp()
        }
    }
}

@Composable
fun NaydiVeschApp() {
    val context = LocalContext.current
    val repository = remember {
        NaydiVeschRepository.getInstance(context, NaydiVeschApi.getInstance())
    }

    var tab by remember { mutableStateOf(0) }

    val thingsVm: ThingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ThingsViewModelFactory(repository).create(modelClass)
        }
    )
    val locationsVm: LocationsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LocationsViewModelFactory(repository).create(modelClass)
        }
    )
    val linkVm: SelectLinkViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SelectLinkViewModelFactory(repository).create(modelClass)
        }
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("НайдиВещь", fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            ) { innerPadding ->
                when (tab) {
                    0 -> ThingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = thingsVm,
                        onNavigateToLocations = { tab = 1 },
                        onThingClick = {},
                        onAddThing = { name, desc, cat, photo ->
                            thingsVm.addThing(name, desc, cat, photo)
                        },
                        onSync = { thingsVm.sync() }
                    )
                    1 -> LocationsScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = locationsVm,
                        onNavigateToThings = { tab = 0 },
                        onLocationClick = {},
                        onAddLocation = { name, desc, nfc, qr ->
                            locationsVm.addLocation(name, desc, nfc, qr)
                        },
                        onSync = { locationsVm.sync() }
                    )
                    else -> LinkScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = linkVm,
                        onSuccess = { tab = 0 }
                    )
                }
            }
        }
    }
}