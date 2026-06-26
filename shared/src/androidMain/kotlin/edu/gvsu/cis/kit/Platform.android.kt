package edu.gvsu.cis.kit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.net.toUri
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.core.content.edit
import java.util.Calendar

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
    val store = getKeyValueStore()
    val hour = store.getInt("pref_reminder_hour", 9)
    val minute = store.getInt("pref_reminder_minute", 0)

    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0) // Explicitly zero out for exactness
    }

    // If the time has already passed today, schedule for tomorrow
    if (target.before(now)) {
        target.add(Calendar.DAY_OF_YEAR, 1)
    }

    val initialDelay = target.timeInMillis - now.timeInMillis

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    // Enqueue or update the exact daily scheduled task using REPLACE
    WorkManager.getInstance(appContext).enqueueUniqueWork(
        "ExactDailyReminder",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}

class AndroidKeyValueStore(context: Context) : KeyValueStore {
    private val prefs: SharedPreferences = context.getSharedPreferences("kit_prefs", Context.MODE_PRIVATE)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    override fun setBoolean(key: String, value: Boolean) { prefs.edit { putBoolean(key, value) } }

    override fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)
    override fun setInt(key: String, value: Int) { prefs.edit { putInt(key, value) } }
}

actual fun getKeyValueStore(): KeyValueStore = AndroidKeyValueStore(appContext)