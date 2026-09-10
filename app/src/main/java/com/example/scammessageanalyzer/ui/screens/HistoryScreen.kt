package com.example.scammessageanalyzer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scammessageanalyzer.data.local.IncidentEntity
import com.example.scammessageanalyzer.ui.components.AppGradientBackground
import com.example.scammessageanalyzer.ui.components.IncidentListItem
import com.example.scammessageanalyzer.ui.components.ScreenIntroCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    incidents: List<IncidentEntity>,
    onMarkAsSafeClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onDeleteAllClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showDeleteAllDialog by rememberSaveable { mutableStateOf(false) }

    AppGradientBackground {
        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text("Delete all history?") },
                text = {
                    Text("This removes every flagged message from the local history on this device.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteAllDialog = false
                            onDeleteAllClick()
                        }
                    ) {
                        Text("Delete all")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Flagged History") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    navigationIcon = {
                        FilledTonalIconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    actions = {
                        FilledTonalIconButton(
                            onClick = { showDeleteAllDialog = true },
                            enabled = incidents.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete all incidents"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (incidents.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenIntroCard(
                        eyebrow = "History",
                        title = "Nothing flagged yet.",
                        description = "Run a manual check or scan recent SMS and the suspicious items will show up here."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ScreenIntroCard(
                            eyebrow = "History",
                            title = "Review the messages you decided to keep flagged.",
                            description = "Use Mark as safe whenever you are sure a message should no longer stay in the suspicious list."
                        )
                    }
                    items(incidents, key = { it.id }) { incident ->
                        IncidentListItem(
                            incident = incident,
                            onMarkAsSafeClick = { onMarkAsSafeClick(incident.id) },
                            onDeleteClick = { onDeleteClick(incident.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
