package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.data.Contact
import edu.gvsu.cis.kit.viewModels.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    viewModel: ContactsViewModel,
    initialShowAdd: Boolean = false,
    onNavigateToIndividualContact: (String) -> Unit,
    onNavigateToAddContact: () -> Unit, // NEW
    onBack: () -> Unit
) {
    val contacts by viewModel.filteredContacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadContacts()
        if (initialShowAdd) onNavigateToAddContact()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Contacts") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddContact,
                icon = { Icon(Icons.Default.Add, "Add Contact") },
                text = { Text("Add Contact") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { viewModel.updateSearchQuery(it) }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search contacts...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(contacts) { contact ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onNavigateToIndividualContact(contact.id) }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                // TODO: Replace placeholder with AsyncImage loaded from profilePictureUri when available
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(12.dp))
                            }

                            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center) {
                                Text(text = contact.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (!contact.relationshipType.isNullOrBlank()) {
                                    Text(text = contact.relationshipType, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            IconButton(onClick = { viewModel.triggerCall(contact.phoneNumber ?: "") }) { Icon(Icons.Default.Call, "Call") }
                            IconButton(onClick = { viewModel.triggerMessage(contact.phoneNumber ?: "") }) { Icon(Icons.Default.Sms, "Message") }
                            IconButton(onClick = { contactToDelete = contact }) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }

        contactToDelete?.let { contact ->
            AlertDialog(
                onDismissRequest = { contactToDelete = null }, title = { Text("Delete Contact") }, text = { Text("Delete ${contact.name}?") },
                confirmButton = { TextButton(onClick = { viewModel.deleteContact(contact.id); contactToDelete = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
                dismissButton = { TextButton(onClick = { contactToDelete = null }) { Text("Cancel") } }
            )
        }
    }
}