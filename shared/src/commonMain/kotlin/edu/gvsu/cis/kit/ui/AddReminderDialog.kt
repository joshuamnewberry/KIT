package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.data.ReminderFrequencyType
import edu.gvsu.cis.kit.viewModels.RemindersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    viewModel: RemindersViewModel,
    preselectedContactId: String? = null,
    editingReminder: CheckInReminder? = null
) {
    val contacts by viewModel.contacts.collectAsState()

    var reminderMessage by remember { mutableStateOf(editingReminder?.customMessage ?: "") }
    var selectedFrequency by remember {
        mutableStateOf(editingReminder?.frequencyType?.let { ReminderFrequencyType.valueOf(it) } ?: ReminderFrequencyType.MONTHLY)
    }
    var selectedContactId by remember { mutableStateOf(preselectedContactId) }
    var frequencyValue by remember { mutableStateOf<Int?>(editingReminder?.frequencyValue ?: 1) }

    var expandedFrequency by remember { mutableStateOf(false) }
    var expandedContact by remember { mutableStateOf(false) }
    var expandedValuePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingReminder != null) "Edit Reminder" else "New Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = reminderMessage, onValueChange = { reminderMessage = it }, label = { Text("Message") }, singleLine = true)

                ExposedDropdownMenuBox(expanded = expandedFrequency, onExpandedChange = { expandedFrequency = !expandedFrequency }) {
                    OutlinedTextField(
                        value = selectedFrequency.name, onValueChange = {}, readOnly = true, label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrequency) },
                        modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = expandedFrequency, onDismissRequest = { expandedFrequency = false }) {
                        ReminderFrequencyType.entries.forEach { freq ->
                            DropdownMenuItem(text = { Text(freq.name) }, onClick = { selectedFrequency = freq; frequencyValue = if (freq != ReminderFrequencyType.DAILY) 1 else null; expandedFrequency = false })
                        }
                    }
                }

                if (selectedFrequency == ReminderFrequencyType.WEEKLY) {
                    ExposedDropdownMenuBox(expanded = expandedValuePicker, onExpandedChange = { expandedValuePicker = !expandedValuePicker }) {
                        OutlinedTextField(
                            value = frequencyValue?.toString() ?: "Day", onValueChange = {}, readOnly = true, label = { Text("Day of Week (1-7)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedValuePicker) },
                            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = expandedValuePicker, onDismissRequest = { expandedValuePicker = false }) {
                            (1..7).forEach { day -> DropdownMenuItem(text = { Text(day.toString()) }, onClick = { frequencyValue = day; expandedValuePicker = false }) }
                        }
                    }
                } else if (selectedFrequency == ReminderFrequencyType.MONTHLY) {
                    ExposedDropdownMenuBox(expanded = expandedValuePicker, onExpandedChange = { expandedValuePicker = !expandedValuePicker }) {
                        OutlinedTextField(
                            value = frequencyValue?.toString() ?: "Date", onValueChange = {}, readOnly = true, label = { Text("Date of Month") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedValuePicker) },
                            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = expandedValuePicker, onDismissRequest = { expandedValuePicker = false }) {
                            (1..31).forEach { date -> DropdownMenuItem(text = { Text(date.toString()) }, onClick = { frequencyValue = date; expandedValuePicker = false }) }
                        }
                    }
                }

                if (preselectedContactId == null && editingReminder == null) {
                    ExposedDropdownMenuBox(expanded = expandedContact, onExpandedChange = { expandedContact = !expandedContact }) {
                        val selectedName = contacts.find { it.id == selectedContactId }?.name ?: "Select Contact"
                        OutlinedTextField(
                            value = selectedName, onValueChange = {}, readOnly = true, label = { Text("Contact") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedContact) },
                            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = expandedContact, onDismissRequest = { expandedContact = false }) {
                            contacts.forEach { contact -> DropdownMenuItem(text = { Text(contact.name) }, onClick = { selectedContactId = contact.id; expandedContact = false }) }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editingReminder != null) {
                        viewModel.updateReminder(editingReminder.copy(customMessage = reminderMessage, frequencyType = selectedFrequency.name, frequencyValue = frequencyValue))
                    } else {
                        selectedContactId?.let { viewModel.addReminder(it, reminderMessage, selectedFrequency, frequencyValue) }
                    }
                    onDismiss()
                },
                enabled = reminderMessage.isNotBlank() && (editingReminder != null || selectedContactId != null)
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}