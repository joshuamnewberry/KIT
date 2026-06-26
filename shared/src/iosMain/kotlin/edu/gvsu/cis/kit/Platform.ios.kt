package edu.gvsu.cis.kit

import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun initPlatformContext(context: Any) { }

actual fun generateUUID(): String = NSUUID().UUIDString

actual fun getCurrentTimeMillis(): Long =
    (NSDate().timeIntervalSinceReferenceDate * 1000).toLong()

actual fun triggerCallIntent(phoneNumber: String) {
    val url = NSURL(string = "tel:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun triggerSmsIntent(phoneNumber: String) {
    val url = NSURL(string = "sms:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun requestContactImport() {
    // Requires CNContactPickerViewController delegate handling in iOS MainViewController
}

actual fun requestNotificationPermission() {
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.requestAuthorizationWithOptions(
        UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
    ) { granted, error ->
        // Notification permission granted or denied
    }
}

actual fun scheduleBackgroundTasks() {
    // Requires BackgroundTasks framework registration in AppDelegate
}

class IOSKeyValueStore : KeyValueStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else defaultValue
    }
    override fun setBoolean(key: String, value: Boolean) { defaults.setBool(value, forKey = key) }

    override fun getInt(key: String, defaultValue: Int): Int {
        return if (defaults.objectForKey(key) != null) defaults.integerForKey(key).toInt() else defaultValue
    }
    override fun setInt(key: String, value: Int) { defaults.setInteger(value.toLong(), forKey = key) }
}

actual fun getKeyValueStore(): KeyValueStore = IOSKeyValueStore()