package edu.gvsu.cis.kit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

class AndroidPlatform(private val context: Context) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

private lateinit var appContext: Context

actual fun getPlatform(): Platform = AndroidPlatform(appContext)

actual fun initAndroidPlatform(context: Any) {
    appContext = context as Context
}

actual fun triggerCallIntent(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

actual fun requestContactImport() {
    // KIT-46: Implement ContactsContract logic
}

actual fun triggerSmsIntent(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phoneNumber")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

actual fun requestNotificationPermission() {
    // KIT-49: Implement Android POST_NOTIFICATIONS
}

actual fun scheduleBackgroundTasks() {
    // KIT-83: Implement WorkManager
}