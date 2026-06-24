package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.RemindersViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualContactScreen(
    viewModel: ContactsViewModel,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit
) {
    val contact by viewModel.selectedContact.collectAsState()
    val reminders by viewModel.selectedContactReminders.collectAsState()
    val remindersViewModel: RemindersViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isEditMode by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var editName by remember(contact) { mutableStateOf(contact?.name ?: "") }
    var editRelationship by remember(contact) { mutableStateOf(contact?.relationshipType ?: "") }
    var editPhone by remember(contact) { mutableStateOf(TextFieldValue(contact?.phoneNumber ?: "")) }
    var editAddress by remember(contact) { mutableStateOf(contact?.address ?: "") }
    var notesState by remember(contact) { mutableStateOf(contact?.notes ?: "") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Contact" else "Contact Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditMode) isEditMode = false else onBack()
                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = onNavigateToHome) { Icon(Icons.Default.Home, "Go Home") }
                    if (contact != null) {
                        IconButton(onClick = {
                            if (isEditMode) {
                                viewModel.updateContact(contact!!.copy(name = editName, relationshipType = editRelationship, phoneNumber = editPhone.text, address = editAddress))
                            }
                            isEditMode = !isEditMode
                        }) { Icon(if (isEditMode) Icons.Default.Check else Icons.Default.Create, "Toggle Edit") }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (contact == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    if (isEditMode) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) { Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp)) }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editRelationship, onValueChange = { editRelationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editPhone, onValueChange = { editPhone = formatPhoneNumber(it) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) { Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp)) }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(contact?.name ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                if (!contact?.relationshipType.isNullOrBlank()) Text(contact?.relationshipType!!, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(12.dp)); Text(contact?.phoneNumber.takeIf { !it.isNullOrBlank() } ?: "N/A") }
                            Row { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(12.dp)); Text(contact?.address.takeIf { !it.isNullOrBlank() } ?: "N/A") }
                        }
                    }
                }

                if (!isEditMode) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                viewModel.logInteraction(contact!!.id)
                                coroutineScope.launch { snackbarHostState.showSnackbar("Interaction logged!") }
                            }) { Text("Log Interaction") }
                            FilledIconButton(onClick = { viewModel.triggerCall(contact!!.phoneNumber ?: "") }) { Icon(Icons.Default.Call, null) }
                            FilledIconButton(onClick = { viewModel.triggerMessage(contact!!.phoneNumber ?: "") }) { Icon(Icons.Default.Sms, null) }
                        }
                    }

                    item {
                        Text("Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = notesState, onValueChange = { notesState = it },
                            modifier = Modifier.fillMaxWidth().height(110.dp).onFocusChanged { if (!it.isFocused) viewModel.updateNotes(contact!!.id, notesState) },
                            placeholder = { Text("Jot down notes...") }, maxLines = 4
                        )
                    }

                    item {
                        Text("Active Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (reminders.isEmpty()) Text("No reminders set.", style = MaterialTheme.typography.bodyMedium)
                        else reminders.forEach { Text("• ${it.frequencyType} Check-in", modifier = Modifier.padding(vertical = 4.dp)) }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showAddReminderDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("+ Add Reminder") }
                    }
                }
            }

            if (showAddReminderDialog) {
                AddReminderDialog(onDismiss = { showAddReminderDialog = false }, viewModel = remindersViewModel, preselectedContactId = contact?.id)
            }
        }
    }
}

fun formatPhoneNumber(input: TextFieldValue): TextFieldValue {
    val rawText = input.text.filter { it.isDigit() }
    if (rawText.length > 10) return TextFieldValue("+$rawText", TextRange(rawText.length + 1))
    val formatted = buildString {
        rawText.forEachIndexed { index, char ->
            when (index) { 0 -> append("($char"); 2 -> append("$char) "); 5 -> append("$char-"); else -> append(char) }
        }
    }
    return TextFieldValue(formatted, TextRange(formatted.length))
}