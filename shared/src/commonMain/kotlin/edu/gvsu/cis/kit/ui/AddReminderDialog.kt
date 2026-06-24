package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.RemindersViewModel

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    viewModel: RemindersViewModel,
    preselectedContactId: String? = null // Auto-include contact
) {
    // Form States
    var reminderTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = reminderTitle,
                    onValueChange = { reminderTitle = it },
                    label = { Text("Reminder Title") }
                )
                // TODO: Add Frequency Dropdown
                // TODO: Add Contact Picker (pre-select if preselectedContactId != null)
            }
        },
        confirmButton = {
            Button(onClick = {
                // TODO: Call viewModel.addReminder(...)
                onDismiss()
            }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}