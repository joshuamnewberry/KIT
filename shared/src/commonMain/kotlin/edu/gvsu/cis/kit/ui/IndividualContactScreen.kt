package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
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

    var isEditMode by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var editName by remember(contact) { mutableStateOf(contact?.name ?: "") }
    var editRelationship by remember(contact) { mutableStateOf(contact?.relationshipType ?: "") }
    var editPhone by remember(contact) { mutableStateOf(TextFieldValue(contact?.phoneNumber ?: "")) }
    var editAddress by remember(contact) { mutableStateOf(contact?.address ?: "") }
    var editBirthday by remember(contact) { mutableStateOf("") }
    var notesState by remember(contact) { mutableStateOf(contact?.notes ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Contact" else "Contact Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditMode) {
                            editName = contact?.name ?: ""
                            editRelationship = contact?.relationshipType ?: ""
                            editPhone = TextFieldValue(contact?.phoneNumber ?: "")
                            editAddress = contact?.address ?: ""
                            isEditMode = false
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (contact != null) {
                        IconButton(onClick = {
                            if (isEditMode) {
                                viewModel.updateContact(
                                    contact!!.copy(
                                        name = editName,
                                        relationshipType = editRelationship,
                                        phoneNumber = editPhone.text,
                                        address = editAddress
                                    )
                                )
                            }
                            isEditMode = !isEditMode
                        }) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Create,
                                contentDescription = if (isEditMode) "Save" else "Edit"
                            )
                        }
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
                    if (isEditMode) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editRelationship, onValueChange = { editRelationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editPhone, onValueChange = { editPhone = formatPhoneNumber(it, editPhone) }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editBirthday, onValueChange = { editBirthday = it }, label = { Text("Birthday") }, modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(contact?.name ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text(contact?.relationshipType ?: "", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = contact?.phoneNumber.takeIf { !it.isNullOrBlank() } ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = contact?.address.takeIf { !it.isNullOrBlank() } ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = editBirthday.takeIf { it.isNotBlank() } ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { /* TODO */ }) { Text("Log Interaction") }
                        FilledIconButton(onClick = { viewModel.triggerCall(contact!!.phoneNumber ?: "") }) { Icon(Icons.Default.Call, null) }
                        FilledIconButton(onClick = { viewModel.triggerMessage(contact!!.phoneNumber ?: "") }) { Icon(Icons.Default.Sms, null) }
                    }
                }

                item {
                    Text("Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notesState,
                        onValueChange = { notesState = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .onFocusChanged { if (!it.isFocused) viewModel.updateNotes(contact!!.id, notesState) },
                        placeholder = { Text("Jot down some notes here...") },
                        maxLines = 4
                    )
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Active Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (reminders.isEmpty()) {
                            Text("No reminders set.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            reminders.forEach { reminder ->
                                Text("• ${reminder.frequencyType} Check-in", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showAddReminderDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("+ Add Reminder")
                        }
                    }
                }
            }

            if (showAddReminderDialog) {
                AddReminderDialog(
                    onDismiss = { showAddReminderDialog = false },
                    viewModel = remindersViewModel,
                    preselectedContactId = contact?.id
                )
            }
        }
    }
}

fun formatPhoneNumber(input: TextFieldValue, old: TextFieldValue): TextFieldValue {
    val rawText = input.text.filter { it.isDigit() }

    if (rawText.length > 10) {
        return TextFieldValue(text = "+$rawText", selection = TextRange(rawText.length + 1))
    }

    val formatted = buildString {
        rawText.forEachIndexed { index, char ->
            when (index) {
                0 -> append("($char")
                2 -> append("$char) ")
                5 -> append("$char-")
                else -> append(char)
            }
        }
    }

    val rawCursor = input.selection.start
    val digitsBeforeCursor = input.text.take(rawCursor).filter { it.isDigit() }.length

    var newCursor = 0
    var digitsCount = 0
    for (i in formatted.indices) {
        if (digitsCount == digitsBeforeCursor) {
            newCursor = i
            break
        }
        if (formatted[i].isDigit()) digitsCount++
        newCursor = i + 1
    }

    return TextFieldValue(text = formatted, selection = TextRange(newCursor.coerceIn(0, formatted.length)))
}