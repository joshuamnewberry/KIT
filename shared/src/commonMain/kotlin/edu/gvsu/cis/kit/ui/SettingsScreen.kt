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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            // Appearance Category
            item {
                SettingsCategoryHeader("Appearance")
            }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Settings,
                    title = "Dark Theme",
                    subtitle = "Use dark colors throughout the app",
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            // Notifications Category
            item {
                SettingsCategoryHeader("Notifications")
            }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Daily Digest",
                    subtitle = "Get a morning summary of who to contact",
                    checked = dailyDigestEnabled,
                    onCheckedChange = { viewModel.toggleDailyDigest(it) }
                )
            }
            item {
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Push Alerts",
                    subtitle = "Immediate notifications for due check-ins",
                    checked = pushAlertsEnabled,
                    onCheckedChange = { viewModel.togglePushAlerts(it) }
                )
            }

            // Data & Privacy Category
            item {
                SettingsCategoryHeader("Data & Privacy")
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Person,
                    title = "Export Backup",
                    subtitle = "Save your contacts and history to a file",
                    onClick = { viewModel.exportBackup() }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Person,
                    title = "Import Backup",
                    subtitle = "Restore your data from a file",
                    onClick = { viewModel.importBackup() }
                )
            }
            item {
                SettingsActionRow(
                    icon = Icons.Default.Info, // Placeholder icon
                    title = "Clear All Data",
                    subtitle = "Permanently delete all contacts and history",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.clearAllData() }
                )
            }

            // About Category
            item {
                SettingsCategoryHeader("About")
            }
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}