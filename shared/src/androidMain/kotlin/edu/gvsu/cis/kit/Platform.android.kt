package edu.gvsu.cis.kit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.net.toUri
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.core.content.edit

object AndroidActivityHooks {
    var launchContactPicker: (() -> Unit)? = null
    var requestNotificationPermission: (() -> Unit)? = null
}

class AndroidPlatform(private val context: Context) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

private lateinit var appContext: Context

actual fun getPlatform(): Platform = AndroidPlatform(appContext)

actual fun initPlatformContext(context: Any) {
    appContext = context as Context
}

actual fun generateUUID(): String = UUID.randomUUID().toString()

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual fun triggerCallIntent(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

actual fun triggerSmsIntent(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "smsto:$phoneNumber".toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

actual fun requestContactImport() {
    AndroidActivityHooks.launchContactPicker?.invoke()
}

actual fun requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AndroidActivityHooks.requestNotificationPermission?.invoke()
    }
}

actual fun scheduleBackgroundTasks() {
    val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES).build()

    WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
        "ReminderCheck",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}

class AndroidKeyValueStore(context: Context) : KeyValueStore {
    private val prefs: SharedPreferences = context.getSharedPreferences("kit_prefs", Context.MODE_PRIVATE)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    override fun setBoolean(key: String, value: Boolean) { prefs.edit { putBoolean(key, value) } }
}

actual fun getKeyValueStore(): KeyValueStore = AndroidKeyValueStore(appContext)