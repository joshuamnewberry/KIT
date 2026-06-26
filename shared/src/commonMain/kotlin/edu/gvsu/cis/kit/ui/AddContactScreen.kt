package edu.gvsu.cis.kit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import edu.gvsu.cis.kit.rememberCameraManager
import edu.gvsu.cis.kit.rememberImagePickerManager
import edu.gvsu.cis.kit.toImageBitmap
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import io.ktor.util.encodeBase64
import kotlin.io.encoding.Base64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    viewModel: ContactsViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    var profilePictureBytes by remember { mutableStateOf<ByteArray?>(null) }

    val cameraManager = rememberCameraManager { capturedByteArray ->
        if (capturedByteArray != null) {
            profilePictureBytes = capturedByteArray
        }
    }

    val imagePickerManager = rememberImagePickerManager { pickedByteArray ->
        if (pickedByteArray != null) {
            profilePictureBytes = pickedByteArray
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Contact") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Profile Picture UI
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                if (profilePictureBytes != null) {
                    val bitmap = remember(profilePictureBytes) { profilePictureBytes!!.toImageBitmap() }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Contact Avatar",
                            modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "No Avatar",
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                OutlinedButton(onClick = { cameraManager.launchCamera() }) {
                    Icon(Icons.Default.PhotoCamera, "Take Photo", modifier = Modifier.padding(end = 8.dp))
                    Text("Camera")
                }
                OutlinedButton(onClick = { imagePickerManager.launchImagePicker() }) {
                    Icon(Icons.Default.Image, "Pick Gallery", modifier = Modifier.padding(end = 8.dp))
                    Text("Gallery")
                }
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it; nameError = false },
                label = { Text("Name") }, isError = nameError, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        val base64Uri = profilePictureBytes?.let { Base64.encode(it) }
                        viewModel.addContact(name, phone, email, relationship, base64Uri)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) { Text("Save Contact") }
        }
    }
}