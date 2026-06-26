package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.HomeViewModel
import edu.gvsu.cis.kit.requestNotificationPermission
import edu.gvsu.cis.kit.scheduleBackgroundTasks
import edu.gvsu.cis.kit.requestContactImport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val pushAlerts by viewModel.pushAlertsEnabled.collectAsState()
    val reminderHour by viewModel.reminderHour.collectAsState()
    val reminderMinute by viewModel.reminderMinute.collectAsState()

    var showTimePicker by remember { mutableStateOf(false) }
    var showWipeConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dark Theme", style = MaterialTheme.typography.titleMedium)
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode(it) })
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Push Alerts", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = pushAlerts,
                    onCheckedChange = {
                        viewModel.togglePushAlerts(it)
                        if (it) requestNotificationPermission()
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Global Reminder Time", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showTimePicker = true }) {
                    val timeString = "${reminderHour.toString().padStart(2, '0')}:${reminderMinute.toString().padStart(2, '0')}"
                    Text(timeString, style = MaterialTheme.typography.titleMedium)
                }
            }

            HorizontalDivider()

            Button(onClick = { requestContactImport() }, modifier = Modifier.fillMaxWidth()) { Text("Import Device Contacts") }
            Button(onClick = { scheduleBackgroundTasks() }, modifier = Modifier.fillMaxWidth()) { Text("Enable Background Sync") }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { showWipeConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Clear All Data") }
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(initialHour = reminderHour, initialMinute = reminderMinute)
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Select Daily Check-In Time") },
                text = { TimePicker(state = timePickerState) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateReminderTime(timePickerState.hour, timePickerState.minute)
                        scheduleBackgroundTasks()
                        showTimePicker = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
            )
        }

        if (showWipeConfirm) {
            AlertDialog(
                onDismissRequest = { showWipeConfirm = false },
                title = { Text("Wipe Application Data") },
                text = { Text("Are you completely sure? This will delete all contacts and reminders.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearAllData(); showWipeConfirm = false }) { Text("Wipe Everything", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { showWipeConfirm = false }) { Text("Cancel") } }
            )
        }
    }
}