package com.example.naydivesch.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainText
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.MenuAnchor
import com.example.naydivesch.R
import com.example.naydivesch.data.model.Link
import com.example.naydivesch.data.model.Thing
import com.example.naydivesch.ui.theme.PrimaryDark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ======================== THINGS SCREEN ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThingsScreen(
    things: List<Thing>,
    onThingClick: (Thing) -> Unit,
    onAddThing: () -> Unit,
    onEditThing: (Thing) -> Unit,
    onDeleteThing: (Thing) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onThingSelected: (Thing) -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredThings = if (searchQuery.isBlank()) {
        things
    } else {
        things.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                (it.description?.contains(searchQuery, ignoreCase = true) ?: false) ||
                it.category?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (filteredThings.isEmpty()) {
            EmptyState(
                iconRes = R.drawable.ic_thing_placeholder,
                message = stringResource(R.string.things_empty),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredThings, key = { it.id }) { thing ->
                    ThingCardItem(
                        thing = thing,
                        onClick = { onThingClick(thing) },
                        onEditClick = { onEditThing(thing) },
                        onDeleteClick = { onDeleteThing(thing) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text(stringResource(R.string.things_search_hint)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .fillMaxWidth(0.92f)
        )

        FloatingActionButton(
            onClick = onAddThing,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.things_add_thing)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThingCardItem(
    thing: Thing,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ThingPhotoPlaceholder(
                    contentDescription = stringResource(R.string.photo_placeholder),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = thing.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                thing.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    thing.category?.let { category ->
                        AssistChip(
                            onClick = { },
                            label = { Text(category, fontSize = 11.sp) },
                            modifier = Modifier.height(20.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                    StatusChip(status = thing.status)
                }
            }

            if (showActions) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.things_edit),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.things_delete),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (container, label) = when (status.lowercase()) {
        "lost" -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        "found" -> Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    }

    AssistChip(
        onClick = { },
        label = {
            Text(
                text = when (status.lowercase()) {
                    "lost" -> stringResource(R.string.thing_status_lost)
                    "found" -> stringResource(R.string.thing_status_found)
                    else -> stringResource(R.string.thing_status_active)
                },
                color = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        },
        modifier = Modifier.height(20.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = container
        )
    )
}

@Composable
fun ThingPhotoPlaceholder(
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ThingForm(
    initialThing: Thing? = null,
    onSave: (Thing) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf(initialThing?.name ?: "") }
    var description by rememberSaveable { mutableStateOf(initialThing?.description ?: "") }
    var category by rememberSaveable { mutableStateOf(initialThing?.category ?: "") }
    var status by rememberSaveable { mutableStateOf(initialThing?.status ?: "active") }

    val statusOptions = listOf(
        "active" to stringResource(R.string.thing_status_active),
        "lost" to stringResource(R.string.thing_status_lost),
        "found" to stringResource(R.string.thing_status_found)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.thing_name_hint)) },
            singleLine = true,
            isError = name.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            supportingText = { if (name.isBlank()) Text("Введите название", color = MaterialTheme.colorScheme.error) }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.thing_description_hint)) },
            singleLine = false,
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text(stringResource(R.string.thing_category_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = statusOptions.find { it.first == status }?.second ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text("Статус") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.thing_cancel))
            }
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val thing = initialThing?.copy(
                            name = name,
                            description = description.ifEmpty { null },
                            category = category.ifEmpty { null },
                            status = status
                        ) ?: Thing(
                            name = name,
                            description = description.ifEmpty { null },
                            category = category.ifEmpty { null },
                            status = status
                        )
                        onSave(thing)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.thing_save))
            }
        }
    }
}

// ======================== LOCATIONS SCREEN ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    locations: List<com.example.naydivesch.data.model.Location>,
    onLocationClick: (com.example.naydivesch.data.model.Location) -> Unit,
    onAddLocation: () -> Unit,
    onEditLocation: (com.example.naydivesch.data.model.Location) -> Unit,
    onDeleteLocation: (com.example.naydivesch.data.model.Location) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredLocations = if (searchQuery.isBlank()) {
        locations
    } else {
        locations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.address?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (filteredLocations.isEmpty()) {
            EmptyState(
                iconRes = R.drawable.ic_location_placeholder,
                message = stringResource(R.string.locations_empty),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLocations, key = { it.id }) { location ->
                    LocationCardItem(
                        location = location,
                        onClick = { onLocationClick(location) },
                        onEditClick = { onEditLocation(location) },
                        onDeleteClick = { onDeleteLocation(location) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Поиск мест...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .fillMaxWidth(0.92f)
        )

        FloatingActionButton(
            onClick = onAddLocation,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.locations_add_location)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCardItem(
    location: com.example.naydivesch.data.model.Location,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            location.address?.let { address ->
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            location.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScanBadge(
                    icon = Icons.Default.QrCodeScanner,
                    label = "QR",
                    onClick = { },
                    colors = ScanBadgeColors.Qr
                )
                ScanBadge(
                    icon = Icons.Default.MoreVert,
                    label = "NFC",
                    onClick = { },
                    colors = ScanBadgeColors.Nfc
                )
            }
        }
    }
}

@Composable
fun NfcTagChip(tagId: String) {
    val shortId = if (tagId.length > 8) tagId.substring(0, 8) + "..." else tagId
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = "NFC: $shortId",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        modifier = Modifier.height(24.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
}

enum class ScanBadgeColors {
    Qr, Nfc
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanBadge(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    colors: ScanBadgeColors
) {
    val (containerColor, labelColor) = when (colors) {
        ScanBadgeColors.Qr -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ScanBadgeColors.Nfc -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    ElevatedAssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = labelColor
            )
        },
        modifier = Modifier.height(28.dp),
        colors = AssistChipDefaults.elevatedAssistChipColors(
            containerColor = containerColor
        )
    )
}

@Composable
fun LocationForm(
    initialLocation: com.example.naydivesch.data.model.Location? = null,
    onSave: (com.example.naydivesch.data.model.Location) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf(initialLocation?.name ?: "") }
    var address by rememberSaveable { mutableStateOf(initialLocation?.address ?: "") }
    var description by rememberSaveable { mutableStateOf(initialLocation?.description ?: "") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.location_name_hint)) },
            singleLine = true,
            isError = name.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.location_address_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.thing_description_hint)) },
            singleLine = false,
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        NfcQrScannerCard(
            onNfcScan = { },
            onQrScan = { }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val location = initialLocation?.copy(
                            name = name,
                            address = address.ifEmpty { null },
                            description = description.ifEmpty { null }
                        ) ?: com.example.naydivesch.data.model.Location(
                            name = name,
                            address = address.ifEmpty { null },
                            description = description.ifEmpty { null }
                        )
                        onSave(location)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
fun NfcQrScannerCard(
    onNfcScan: () -> Unit,
    onQrScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Сканирование меток",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNfcScan,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text("NFC", modifier = Modifier.padding(start = 6.dp))
                }
                OutlinedButton(
                    onClick = onQrScan,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text("QR", modifier = Modifier.padding(start = 6.dp))
                }
            }

            Text(
                text = stringResource(R.string.location_nfc_instructions),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ======================== LINK SCREEN ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkScreen(
    things: List<Thing>,
    locations: List<com.example.naydivesch.data.model.Location>,
    existingLinks: List<Link>,
    onLinkCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedThing by rememberSaveable { mutableStateOf<Thing?>(null) }
    var expandedThingMenu by remember { mutableStateOf(false) }

    var selectedLocation by rememberSaveable { mutableStateOf<com.example.naydivesch.data.model.Location?>(null) }
    var expandedLocationMenu by remember { mutableStateOf(false) }

    var notes by rememberSaveable { mutableStateOf("") }
    var linkType by rememberSaveable { mutableStateOf("current") }

    val linkTypeOptions = listOf(
        "current" to stringResource(R.string.link_type_current),
        "stored" to stringResource(R.string.link_type_stored),
        "lost_at" to stringResource(R.string.link_type_lost_at)
    )

    val canSave = selectedThing != null && selectedLocation != null

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.link_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (canSave) {
                        val link = Link(
                            thingId = selectedThing!!.id,
                            locationId = selectedLocation!!.id,
                            linkType = linkType,
                            notes = notes.ifEmpty { null }
                        )
                        onLinkCreated()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                enabled = canSave
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.link_save)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedThingMenu,
                onExpandedChange = { expandedThingMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedThing?.name ?: stringResource(R.string.link_thing_placeholder),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.link_select_thing)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedThingMenu)
                    },
                    isError = selectedThing == null,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedThingMenu,
                    onDismissRequest = { expandedThingMenu = false }
                ) {
                    things.forEach { thing ->
                        DropdownMenuItem(
                            text = { Text(thing.name) },
                            onClick = {
                                selectedThing = thing
                                expandedThingMenu = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedLocationMenu,
                onExpandedChange = { expandedLocationMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLocation?.name ?: stringResource(R.string.link_location_placeholder),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.link_select_location)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocationMenu)
                    },
                    isError = selectedLocation == null,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedLocationMenu,
                    onDismissRequest = { expandedLocationMenu = false }
                ) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location.name) },
                            onClick = {
                                selectedLocation = location
                                expandedLocationMenu = false
                            }
                        )
                    }
                }
            }

            LinkTypeSelector(
                options = linkTypeOptions,
                selectedType = linkType,
                onTypeSelected = { linkType = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.link_notes_hint)) },
                singleLine = false,
                minLines = 3,
                placeholder = { Text("Введите примечание...") },
                modifier = Modifier.fillMaxWidth()
            )

            if (existingLinks.any {
                    it.thingId == selectedThing?.id && it.locationId == selectedLocation?.id
                }) {
                Text(
                    text = stringResource(R.string.link_already_exists),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LinkTypeSelector(
    options: List<Pair<String, String>>,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Тип связи",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        options.forEach { (value, label) ->
            RadioButtonRow(
                selected = selectedType == value,
                text = label,
                onClick = { onTypeSelected(value) }
            )
        }
    }
}

@Composable
fun RadioButtonRow(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = androidx.compose.material3.RadioButtonDefaults.radioButtonColors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ======================== SHARED COMPONENTS ========================

@Composable
fun EmptyState(
    iconRes: Int?,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (iconRes != null) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        onAction?.let { action ->
            OutlinedButton(onClick = action) {
                Text(actionLabel ?: "Выполнить")
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    thingName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.thing_confirm_delete)) },
        text = { Text("Вещь \"$thingName\" будет удалена безвозвратно. Продолжить?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.thing_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.thing_cancel))
            }
        }
    )
}

@Composable
fun MainTopAppBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = if (onNavigationClick != null) {
            {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else null,
        actions = {
            actions?.let {
                Row(modifier = Modifier) {
                    it()
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

@Composable
fun MainBottomBar(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = tab.iconVector,
                        contentDescription = tab.label,
                        tint = if (selectedIndex == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
        }
    }
}

// ======================== HELPERS ========================

data class TabItem(
    val label: String,
    val route: String,
    val index: Int,
    val iconVector: ImageVector
)
