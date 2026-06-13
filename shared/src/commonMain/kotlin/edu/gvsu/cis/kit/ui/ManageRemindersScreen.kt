package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.CalendarViewModel

@Composable
fun ManageRemindersScreen(
    viewModel: CalendarViewModel,
    onBack: () -> Unit
) {
    var selectedReminder by remember {
        mutableStateOf("Weekly")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Manage Reminders",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Choose how often you would like to be reminded to contact someone."
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Reminder")
        }
    }
}

@Composable
private fun ReminderOption(
    title: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        RadioButton(
            selected = selected,
            onClick = onSelected
        )

        Text(
            text = title,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
