package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.ContactsViewModel

@Composable
fun ContactListScreen(
    viewModel: ContactsViewModel,
    onNavigateToIndividualContact: () -> Unit,
    onBack: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadContacts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Contacts",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = relationship,
            onValueChange = { relationship = it },
            label = { Text("Relationship Type") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addContact(name, phone, email, relationship)
                        name = ""
                        phone = ""
                        email = ""
                        relationship = ""
                    }
                }
            ) {
                Text("Add Contact")
            }

            Button(onClick = onBack) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(contacts) { contact ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToIndividualContact
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(text = contact.relationshipType ?: "No relationship type")

                        if (!contact.phoneNumber.isNullOrBlank()) {
                            Text(text = contact.phoneNumber)
                        }

                        if (!contact.email.isNullOrBlank()) {
                            Text(text = contact.email)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.addWeeklyReminder(contact.id)
                                }
                            ) {
                                Text("Weekly")
                            }

                            Button(
                                onClick = {
                                    viewModel.addMonthlyReminder(contact.id)
                                }
                            ) {
                                Text("Monthly")
                            }

                            Button(
                                onClick = {
                                    viewModel.addQuarterlyReminder(contact.id)
                                }
                            ) {
                                Text("Quarterly")
                            }
                        }
                    }
                }
            }
        }
    }
}
