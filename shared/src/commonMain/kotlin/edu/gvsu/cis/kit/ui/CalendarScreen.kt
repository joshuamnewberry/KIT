package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    initialShowAdd: Boolean = false,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(initialShowAdd) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Event") },
                text = { Text("Add Event") },
                modifier = Modifier.offset(y = 12.dp)
            )
        }
    ) { paddingValues ->
        Text(
            text = "Calendar View Under Construction",
            modifier = Modifier.padding(paddingValues)
        )

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Event") },
                text = {
                    Text("Event creation form will go here.")
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