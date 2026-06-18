package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRemindersScreen(
    viewModel: CalendarViewModel,
    initialShowAdd: Boolean = false,
    onBack: () -> Unit
) {
    var selectedReminder by remember { mutableStateOf("Weekly") }
    var showAddDialog by remember { mutableStateOf(initialShowAdd) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Reminders") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Reminder") },
                text = { Text("Add Reminder") },
                modifier = Modifier.offset(y = 12.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Choose how often you would like to be reminded to contact someone.")

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ReminderOption(
                        title = "Weekly",
                        selected = selectedReminder == "Weekly"
                    ) {
                        selectedReminder = "Weekly"
                    }
                    ReminderOption(
                        title = "Monthly",
                        selected = selectedReminder == "Monthly"
                    ) {
                        selectedReminder = "Monthly"
                    }
                    ReminderOption(
                        title = "Quarterly",
                        selected = selectedReminder == "Quarterly"
                    ) {
                        selectedReminder = "Quarterly"
                    }
                }
            }

            Text(
                text = "Selected: $selectedReminder",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Reminder") },
                text = {
                    Text("Reminder creation form will go here.")
                },
                confirmButton = {
                    Button(onClick = { showAddDialog = false }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ReminderOption(
    title: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        RadioButton(selected = selected, onClick = onSelected)
        Text(text = title, modifier = Modifier.padding(top = 12.dp))
    }
}