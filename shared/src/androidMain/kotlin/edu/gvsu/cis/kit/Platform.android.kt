package edu.gvsu.cis.kit

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun requestContactImport() {
    // TODO: Implement Android ActivityResultLauncher for ContactsContract (KIT-46)
}

actual fun triggerCallIntent(phoneNumber: String) {
    // TODO: Implement Android Intent.ACTION_DIAL (KIT-47)
}

actual fun triggerSmsIntent(phoneNumber: String) {
    // TODO: Implement Android Intent.ACTION_SENDTO (KIT-48)
}

actual fun requestNotificationPermission() {
    // TODO: Implement Android POST_NOTIFICATIONS permission request (KIT-49)
}

actual fun scheduleBackgroundTasks() {
    // TODO: Implement Android WorkManager task scheduling (KIT-83)
}