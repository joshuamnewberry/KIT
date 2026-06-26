package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.rememberCameraManager
import edu.gvsu.cis.kit.rememberImagePickerManager
import edu.gvsu.cis.kit.toImageBitmap
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.RemindersViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64

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
    var editPhone by remember(contact) { mutableStateOf(TextFieldValue(formatPhoneNumberString(contact?.phoneNumber))) }
    var editAddress by remember(contact) { mutableStateOf(contact?.address ?: "") }
    var editBirthday by remember(contact) { mutableStateOf(contact?.birthday ?: "") }
    var notesState by remember(contact) { mutableStateOf(contact?.notes ?: "") }
    var isNotesFocused by remember { mutableStateOf(false) }

    var profilePictureBytes by remember(contact) {
        mutableStateOf(
            try {
                contact?.profilePictureUri?.let { Base64.decode(it) }
            } catch (_: Exception) { null }
        )
    }

    val cameraManager = rememberCameraManager { bytes -> if (bytes != null) profilePictureBytes = bytes }
    val imagePickerManager = rememberImagePickerManager { bytes -> if (bytes != null) profilePictureBytes = bytes }

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
                                viewModel.updateContact(contact!!.copy(
                                    name = editName,
                                    relationshipType = editRelationship,
                                    phoneNumber = editPhone.text,
                                    address = editAddress,
                                    birthday = editBirthday,
                                    profilePictureUri = profilePictureBytes?.let { Base64.encode(it) }
                                ))
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
                            Box(modifier = Modifier.size(100.dp).padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
                                if (profilePictureBytes != null) {
                                    val bitmap = remember(profilePictureBytes) { profilePictureBytes!!.toImageBitmap() }
                                    if (bitmap != null) {
                                        Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), contentScale = ContentScale.Crop)
                                    }
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = "No Avatar", modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                                OutlinedButton(onClick = { cameraManager.launchCamera() }) { Icon(Icons.Default.PhotoCamera, "Camera", modifier = Modifier.padding(end = 8.dp)); Text("Camera") }
                                OutlinedButton(onClick = { imagePickerManager.launchImagePicker() }) { Icon(Icons.Default.Image, "Gallery", modifier = Modifier.padding(end = 8.dp)); Text("Gallery") }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editRelationship, onValueChange = { editRelationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editPhone, onValueChange = { editPhone = formatPhoneNumber(it, editPhone) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = editBirthday, onValueChange = { editBirthday = it }, label = { Text("Birthday") }, modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val displayBytes = remember(contact?.profilePictureUri) { contact?.profilePictureUri?.let {
                                Base64.decode(
                                    it
                                )
                            } }
                            if (displayBytes != null) {
                                val bitmap = remember(displayBytes) { displayBytes.toImageBitmap() }
                                if (bitmap != null) {
                                    Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(72.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape), contentScale = ContentScale.Crop)
                                }
                            } else {
                                Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) { Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp)) }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(editName.ifBlank { "Unknown" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                if (editRelationship.isNotBlank()) Text(editRelationship, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val displayPhone = editPhone.text.takeIf { it.isNotBlank() } ?: "N/A"
                            Row { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(12.dp)); Text(displayPhone) }
                            Row { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(12.dp)); Text(editAddress.takeIf { it.isNotBlank() } ?: "N/A") }
                            Row { Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.width(12.dp)); Text(editBirthday.takeIf { it.isNotBlank() } ?: "N/A") }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .onFocusChanged { focusState ->
                                    if (isNotesFocused && !focusState.isFocused) {
                                        viewModel.updateNotes(contact!!.id, notesState)
                                    }
                                    isNotesFocused = focusState.isFocused
                                },
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

fun formatPhoneNumberString(raw: String?): String {
    if (raw == null) return ""
    val digits = raw.filter { it.isDigit() }

    if (digits.length > 10) return "+$digits"
    if (digits.isEmpty()) return ""

    val formatted = buildString {
        digits.forEachIndexed { index, char ->
            when (index) {
                0 -> append("($char")
                2 -> append("$char) ")
                5 -> append("$char-")
                else -> append(char)
            }
        }
    }

    return if (formatted.endsWith("-")) formatted.dropLast(1)
    else if (formatted.endsWith(") ")) formatted.dropLast(2)
    else if (formatted.endsWith("(")) formatted.dropLast(1)
    else formatted
}

fun formatPhoneNumber(input: TextFieldValue, old: TextFieldValue): TextFieldValue {
    val rawText = input.text.filter { it.isDigit() }
    val isDeleting = input.text.length < old.text.length

    var digitsToFormat = rawText
    val oldRaw = old.text.filter { it.isDigit() }

    if (isDeleting && oldRaw == rawText && rawText.isNotEmpty()) {
        digitsToFormat = rawText.dropLast(1)
    }

    val rawCursor = input.selection.start
    val digitsBeforeCursor = input.text.take(rawCursor).filter { it.isDigit() }.length

    if (digitsToFormat.length > 10) {
        val newCursor = if (digitsBeforeCursor == digitsToFormat.length) digitsToFormat.length + 1 else digitsBeforeCursor + 1
        return TextFieldValue(text = "+$digitsToFormat", selection = TextRange(newCursor.coerceIn(0, digitsToFormat.length + 1)))
    }

    var formatted = buildString {
        digitsToFormat.forEachIndexed { index, char ->
            when (index) {
                0 -> append("($char")
                2 -> append("$char) ")
                5 -> append("$char-")
                else -> append(char)
            }
        }
    }

    if (isDeleting) {
        if (formatted.endsWith("-")) formatted = formatted.dropLast(1)
        else if (formatted.endsWith(") ")) formatted = formatted.dropLast(2)
        else if (formatted.endsWith("(")) formatted = formatted.dropLast(1)
    }

    var newCursor = 0
    if (digitsBeforeCursor == digitsToFormat.length) {
        newCursor = formatted.length
    } else if (digitsBeforeCursor == 0) {
        newCursor = if (formatted.startsWith("(")) 1 else 0
    } else {
        var digitsCount = 0
        for (i in formatted.indices) {
            if (formatted[i].isDigit()) {
                digitsCount++
                if (digitsCount == digitsBeforeCursor) {
                    newCursor = i + 1
                    if (!isDeleting) {
                        while (newCursor < formatted.length && !formatted[newCursor].isDigit()) {
                            newCursor++
                        }
                    }
                    break
                }
            }
        }
    }

    return TextFieldValue(text = formatted, selection = TextRange(newCursor.coerceIn(0, formatted.length)))
}