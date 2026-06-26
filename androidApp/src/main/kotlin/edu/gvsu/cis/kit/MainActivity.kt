package edu.gvsu.cis.kit

import android.app.Activity
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

class MainActivity : ComponentActivity(), KoinComponent {

    private val contactsViewModel: ContactsViewModel by inject()

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Permission result handled automatically by Android OS
    }

    private val contactPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri = result.data?.data ?: return@registerForActivityResult
            // We request only the columns we need from the implicitly permitted URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val cursor = contentResolver.query(contactUri, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val name = cursor.getString(nameIndex) ?: "Unknown"
                val phoneNumber = cursor.getString(phoneIndex) ?: ""

                cursor.close()

                contactsViewModel.addContact(name, phoneNumber, "", "Imported")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Build the database and extract the DAO
        val dbBuilder = getDatabaseBuilder(this.applicationContext)
        val db = getDatabaseInstance(dbBuilder)
        val dao = db.getDao()

        // 2. Start Koin dependency injection BEFORE loading the UI
        initKoin(dao = dao, context = this.applicationContext)

        // 3. Register the hooks to avoid circular dependencies between shared and androidApp modules
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
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        contactPickerLauncher.launch(intent)
    }
}