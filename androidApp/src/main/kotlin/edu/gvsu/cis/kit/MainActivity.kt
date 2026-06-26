package edu.gvsu.cis.kit

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import edu.gvsu.cis.kit.data.getDatabaseInstance
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.core.net.toUri

class MainActivity : ComponentActivity(), KoinComponent {

    private val contactsViewModel: ContactsViewModel by inject()

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
    }

    private val contactsPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactPickerLauncher.launch(intent)
        }
    }

    private val contactPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val contactUri = result.data?.data ?: return@registerForActivityResult

            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            )

            val cursor = contentResolver.query(contactUri, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                val name = cursor.getString(nameIndex) ?: "Unknown"
                val phoneNumber = cursor.getString(phoneIndex) ?: ""
                val photoUriStr = if (photoIndex != -1) cursor.getString(photoIndex) else null

                cursor.close()

                var base64Image: String? = null
                if (photoUriStr != null) {
                    try {
                        val photoUri = photoUriStr.toUri()
                        val inputStream = contentResolver.openInputStream(photoUri)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP).replace("\n", "").replace("\r", "")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                contactsViewModel.addContact(name, phoneNumber, "Imported", "", "", base64Image)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbBuilder = getDatabaseBuilder(this.applicationContext)
        val db = getDatabaseInstance(dbBuilder)
        val dao = db.getDao()

        initKoin(dao = dao, context = this.applicationContext)

        AndroidActivityHooks.launchContactPicker = { launchOSContactPicker() }
        AndroidActivityHooks.requestNotificationPermission = { requestOSNotificationPermission() }

        setContent {
            App()
        }
    }

    private fun requestOSNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun launchOSContactPicker() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactPickerLauncher.launch(intent)
        } else {
            contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }
}