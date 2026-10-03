package com.example.naydivesch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Settings as SettingsIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.window.Dialog
import com.example.naydivesch.data.database.DatabaseProvider
import com.example.naydivesch.ui.MainViewModel
import com.example.naydivesch.ui.screens.ConfirmDeleteDialog
import com.example.naydivesch.ui.screens.LocationsScreen
import com.example.naydivesch.ui.screens.LinkScreen
import com.example.naydivesch.ui.screens.ThingsScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NaydiVeschApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseProvider.closeDatabase()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NaydiVeschApp() {
    val navController = rememberNavController()
    val selectedTab = remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val viewModel: MainViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(context) as T
            }
        }
    )

    val tabs = listOf(
        TabItem("Главная", "home", 0, Icons.Default.Home),
        TabItem("Вещи", "things", 1, Icons.Default.Search),
        TabItem("Поиск", "search", 2, Icons.Default.LocationOn),
        TabItem("Связи", "links", 3, Icons.Default.Settings),
        TabItem("Настройки", "settings", 4, SettingsIcon)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(text = "НайдиВещь", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                if (selectedTab.value !in listOf(4)) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                icon = { Text(text = tab.iconChar) },
                                label = { Text(text = tab.label, fontSize = 10.sp) },
                                selected = selectedTab.value == tab.index,
                                onClick = {
                                    selectedTab.value = tab.index
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(onNavigateToSearch = { selectedTab.value = 2; navController.navigate("search") }) }
                composable("search") { SearchScreen() }
                composable("settings") { SettingsScreen() }
                composable("things") { ThingsTabContent(viewModel = viewModel, snackbarHostState = snackbarHostState) }
                composable("locations") { LocationsTabContent(viewModel = viewModel, snackbarHostState = snackbarHostState) }
                composable("links") { LinkTabContent(viewModel = viewModel, snackbarHostState = snackbarHostState) }
            }
        }
    }
}

data class TabItem(val label: String, val route: String, val index: Int, val iconVector: ImageVector) {
    val iconChar = when (route) {
        "home" -> "🏠"
        "things" -> "🔍"
        "search" -> "📍"
        "links" -> "🔗"
        "settings" -> "⚙️"
        else -> "•"
    }
}

// ======================== HOME SCREEN ========================
@Composable
fun HomeScreen(onNavigateToSearch: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Добро пожаловать в НайдиВещь!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Приложение помогает находить потерянные вещи.", fontSize = 16.sp)

                Button(
                    onClick = onNavigateToSearch,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Начать поиск", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "Поиск вещи", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Здесь будет функционал поиска (NFC, QR, голос)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "Настройки", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Настройки приложения", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ======================== THINGS TAB CONTENT ========================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThingsTabContent(viewModel: MainViewModel, snackbarHostState: SnackbarHostState) {
    var showAddForm by remember { mutableStateOf(false) }
    var editingThing by remember { mutableStateOf<com.example.naydivesch.data.model.Thing?>(null) }
    var deletingThing by remember { mutableStateOf<com.example.naydivesch.data.model.Thing?>(null) }

    val thingsFlow = viewModel.allThings.collectAsState(initial = emptyList())
    val things = thingsFlow.value

    ThingsScreen(
        things = things,
        onThingClick = { /* TODO: Navigate to detail */ },
        onAddThing = { showAddForm = true },
        onEditThing = { thing -> editingThing = thing },
        onDeleteThing = { thing -> deletingThing = thing },
        snackbarHostState = snackbarHostState
    )

    if (showAddForm) {
        ThingFormBottomSheet(
            onSave = {
                viewModel.createThing(it.name, it.description, it.locationId, it.category)
                showAddForm = false
                snackbarHostState.showSnackbar("Вещь добавлена")
            },
            onCancel = { showAddForm = false }
        )
    }

    if (editingThing != null) {
        ThingFormBottomSheet(
            initialThing = editingThing,
            onSave = { updatedThing ->
                viewModel.updateThing(updatedThing.id, updatedThing.name, updatedThing.description, updatedThing.locationId, updatedThing.category)
                editingThing = null
                snackbarHostState.showSnackbar("Вещь обновлена")
            },
            onCancel = { editingThing = null }
        )
    }

    if (deletingThing != null) {
        ConfirmDeleteDialog(
            thingName = deletingThing!!.name,
            onConfirm = {
                viewModel.deleteThing(deletingThing!!.id)
                deletingThing = null
                snackbarHostState.showSnackbar("Вещь удалена")
            },
            onDismiss = { deletingThing = null }
        )
    }
}

@Composable
fun ThingFormBottomSheet(
    initialThing: com.example.naydivesch.data.model.Thing? = null,
    onSave: (com.example.naydivesch.data.model.Thing) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            ThingForm(
                initialThing = initialThing,
                onSave = { onSave(it) },
                onCancel = onCancel,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

// ======================== LOCATIONS TAB CONTENT ========================
@Composable
fun LocationsTabContent(viewModel: MainViewModel, snackbarHostState: SnackbarHostState) {
    var showAddForm by remember { mutableStateOf(false) }
    var editingLocation by remember { mutableStateOf<com.example.naydivesch.data.model.Location?>(null) }
    var deletingLocation by remember { mutableStateOf<com.example.naydivesch.data.model.Location?>(null) }

    val locationsFlow = viewModel.allLocations.collectAsState(initial = emptyList())
    val locations = locationsFlow.value

    LocationsScreen(
        locations = locations,
        onLocationClick = { /* TODO: Navigate to detail */ },
        onAddLocation = { showAddForm = true },
        onEditLocation = { location -> editingLocation = location },
        onDeleteLocation = { location -> deletingLocation = location },
        snackbarHostState = snackbarHostState
    )

    if (showAddForm) {
        LocationFormBottomSheet(
            onSave = {
                viewModel.createLocation(it.name, it.address, it.latitude, it.longitude, it.description)
                showAddForm = false
                snackbarHostState.showSnackbar("Место добавлено")
            },
            onCancel = { showAddForm = false }
        )
    }

    if (editingLocation != null) {
        LocationFormBottomSheet(
            initialLocation = editingLocation,
            onSave = { updatedLocation ->
                viewModel.updateLocation(
                    updatedLocation.id,
                    updatedLocation.name,
                    updatedLocation.address,
                    updatedLocation.latitude,
                    updatedLocation.longitude,
                    updatedLocation.description
                )
                editingLocation = null
                snackbarHostState.showSnackbar("Место обновлено")
            },
            onCancel = { editingLocation = null }
        )
    }

    if (deletingLocation != null) {
        AlertDialog(
            onDismissRequest = { deletingLocation = null },
            title = { Text("Удалить место?") },
            text = { Text("\"${deletingLocation!!.name}\" будет удалено безвозвратно.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLocation(deletingLocation!!.id)
                    deletingLocation = null
                    snackbarHostState.showSnackbar("Место удалено")
                }) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { deletingLocation = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun LocationFormBottomSheet(
    initialLocation: com.example.naydivesch.data.model.Location? = null,
    onSave: (com.example.naydivesch.data.model.Location) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            LocationForm(
                initialLocation = initialLocation,
                onSave = { onSave(it) },
                onCancel = onCancel,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

// ======================== LINKS TAB CONTENT ========================
@Composable
fun LinkTabContent(viewModel: MainViewModel, snackbarHostState: SnackbarHostState) {
    val thingsFlow = viewModel.allThings.collectAsState(initial = emptyList())
    val locationsFlow = viewModel.allLocations.collectAsState(initial = emptyList())
    val linksFlow = viewModel.allLinks.collectAsState(initial = emptyList())

    LinkScreen(
        things = thingsFlow.value,
        locations = locationsFlow.value,
        existingLinks = linksFlow.value,
        onLinkCreated = {
            snackbarHostState.showSnackbar("Связь создана")
        }
    )
}
