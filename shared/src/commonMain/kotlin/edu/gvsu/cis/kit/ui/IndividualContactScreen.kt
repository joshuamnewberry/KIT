package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualContactScreen(
    viewModel: ContactsViewModel,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit
) {
    val contact by viewModel.selectedContact.collectAsState()
    val reminders by viewModel.selectedContactReminders.collectAsState()
    val importantDates by viewModel.importantDates.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(contact?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit Contact */ }) {
                        Icon(Icons.Default.Create, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (contact == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Relationship: ${contact?.relationshipType ?: "None"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { /* Log Interaction */ }) {
                            Text("Log Interaction")
                        }
                        IconButton(onClick = { /* Handle Call */ }) {
                            Icon(Icons.Default.Call, contentDescription = "Call")
                        }
                        IconButton(onClick = { /* Handle Email */ }) {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Active Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (reminders.isEmpty()) {
                                Text("No reminders set.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                reminders.forEach { reminder ->
                                    Text("• ${reminder.frequency} Check-in")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { contact?.id?.let { viewModel.addMonthlyReminder(it) } },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("+ Add Monthly Reminder")
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Important Dates",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (importantDates.isEmpty()) {
                                Text("No dates saved.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                importantDates.forEach { date ->
                                    Text("• ${date.title} (${date.type})")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}