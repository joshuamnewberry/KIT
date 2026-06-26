// shared/src/commonMain/kotlin/edu/gvsu/cis/kit/Platform.kt
package edu.gvsu.cis.kit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// UUID & Time Utilities
expect fun generateUUID(): String
expect fun getCurrentTimeMillis(): Long

// Native Integrations
expect fun requestContactImport()
expect fun triggerCallIntent(phoneNumber: String)
expect fun triggerSmsIntent(phoneNumber: String)
expect fun requestNotificationPermission()
expect fun hasNotificationPermission(): Boolean
expect fun scheduleBackgroundTasks()

// Cross-Platform Settings Store
interface KeyValueStore {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun getInt(key: String, defaultValue: Int): Int
    fun setInt(key: String, value: Int)
}

expect fun getKeyValueStore(): KeyValueStore