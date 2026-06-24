package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.viewModels.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRemindersScreen(
    viewModel: RemindersViewModel,
    initialShowAdd: Boolean = false,
    onBack: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(initialShowAdd) }
    var reminderToDelete by remember { mutableStateOf<CheckInReminder?>(null) }
    var reminderToEdit by remember { mutableStateOf<CheckInReminder?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadReminders()
        viewModel.loadContacts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Reminders") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { reminderToEdit = null; showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                icon = { Icon(Icons.Default.Add, "Add Reminder") },
                text = { Text("New Reminder") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reminders active.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(reminders) { (reminder, contacts) ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { reminderToEdit = reminder; showAddDialog = true }) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(reminder.customMessage ?: "Check-in", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("With: ${contacts.joinToString { it.name }}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Repeats: ${reminder.frequencyType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }

                                IconButton(onClick = { reminderToDelete = reminder }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        reminderToDelete?.let { reminder ->
            AlertDialog(
                onDismissRequest = { reminderToDelete = null },
                title = { Text("Delete Reminder") },
                text = { Text("Are you sure you want to delete this reminder?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.deleteReminder(reminder); reminderToDelete = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = { TextButton(onClick = { reminderToDelete = null }) { Text("Cancel") } }
            )
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false; reminderToEdit = null },
                viewModel = viewModel,
                editingReminder = reminderToEdit
            )
        }
    }
}