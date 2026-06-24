package edu.gvsu.cis.kit

import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import platform.Foundation.NSURL

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun requestContactImport() {
    // KIT-46: Implement CNContactPickerViewController
}

actual fun triggerCallIntent(phoneNumber: String) {
    val url = NSURL(string = "tel:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun triggerSmsIntent(phoneNumber: String) {
    val url = NSURL(string = "sms:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun requestNotificationPermission() {
    // KIT-49: Implement UNUserNotificationCenter
}

actual fun scheduleBackgroundTasks() {
    // KIT-83: Implement BGTaskScheduler
}