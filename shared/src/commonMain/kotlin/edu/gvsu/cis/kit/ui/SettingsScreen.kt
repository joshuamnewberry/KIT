package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val dailyDigestEnabled by viewModel.dailyDigestEnabled.collectAsState()
    val pushAlertsEnabled by viewModel.pushAlertsEnabled.collectAsState()

    var message by remember { mutableStateOf<String?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all data?") },
            text = { Text("This will delete all contacts, reminders, and history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        message = "All local data cleared."
                        showClearDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (message != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = message ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item { SettingsCategoryHeader("Appearance") }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Settings,
                    title = "Dark Theme",
                    subtitle = "Use dark colors throughout the app",
                    checked = isDarkMode,
                    onCheckedChange = {
                        viewModel.toggleDarkMode(it)
                        message = if (it) "Dark theme enabled." else "Dark theme disabled."
                    }
                )
            }

            item { SettingsCategoryHeader("Notifications") }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Daily Digest",
                    subtitle = "Get a morning summary of who to contact",
                    checked = dailyDigestEnabled,
                    onCheckedChange = {
                        viewModel.toggleDailyDigest(it)
                        message = if (it) "Daily digest enabled." else "Daily digest disabled."
                    }
                )
            }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Push Alerts",
                    subtitle = "Immediate notifications for due check-ins",
                    checked = pushAlertsEnabled,
                    onCheckedChange = {
                        viewModel.togglePushAlerts(it)
                        message = if (it) "Push alerts enabled." else "Push alerts disabled."
                    }
                )
            }

            item { SettingsCategoryHeader("Data & Privacy") }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Person,
                    title = "Export Backup",
                    subtitle = "Save your contacts and history to a file",
                    onClick = {
                        viewModel.exportBackup()
                        message = "Export backup feature selected."
                    }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Person,
                    title = "Import Backup",
                    subtitle = "Restore your data from a file",
                    onClick = {
                        viewModel.importBackup()
                        message = "Import backup feature selected."
                    }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Info,
                    title = "Clear All Data",
                    subtitle = "Permanently delete all contacts and history",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { showClearDialog = true }
                )
            }

            item { SettingsCategoryHeader("About") }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "KIT (Keep In Touch) Version 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
