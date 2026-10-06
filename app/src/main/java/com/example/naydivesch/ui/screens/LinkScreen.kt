package com.example.naydivesch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naydivesch.model.Location
import com.example.naydivesch.model.Thing
import com.example.naydivesch.ui.viewmodel.SelectLinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectLinkViewModel,
    onSuccess: () -> Unit
) {
    val things by viewModel.allThings.collectAsState()
    val locations by viewModel.allLocations.collectAsState()
    val selectedThing by viewModel.selectedThing.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val canLink by viewModel.canLink.collectAsState()

    var thingExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Привязать вещь к месту",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dropdown вещь
                Text("Выберите вещь:", fontWeight = FontWeight.Medium)
                ExposedDropdownMenuBox(
                    expanded = thingExpanded,
                    onExpandedChange = { thingExpanded = !thingExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedThing?.name ?: "Не выбрано",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = thingExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = thingExpanded,
                        onDismissRequest = { thingExpanded = false }
                    ) {
                        things.forEach { thing ->
                            DropdownMenuItem(
                                text = { Text(thing.name) },
                                onClick = {
                                    viewModel.selectedThing.value = thing
                                    thingExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Dropdown место хранения
                Text("Выберите место хранения:", fontWeight = FontWeight.Medium)
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLocation?.name ?: "Не выбрано",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location.name) },
                                onClick = {
                                    viewModel.selectedLocation.value = location
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.linkSelected()
                onSuccess()
            },
            enabled = canLink,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Связать вещь и место")
        }
    }
}
