package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRemindersScreen(
    viewModel: RemindersViewModel,
    initialShowAdd: Boolean = false,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(initialShowAdd) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Reminders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Reminder") },
                text = { Text("New Reminder") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Your Reminders", style = MaterialTheme.typography.titleLarge)
            // List logic will go here
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                viewModel = viewModel
            )
        }
    }
}