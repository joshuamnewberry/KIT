package edu.gvsu.cis.kit

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun requestContactImport() {
    // TODO: Implement iOS CNContactPickerViewController (KIT-46)
}

actual fun triggerCallIntent(phoneNumber: String) {
    // TODO: Implement iOS openURL with tel:// (KIT-47)
}

actual fun triggerSmsIntent(phoneNumber: String) {
    // TODO: Implement iOS openURL with sms:// or MFMessageComposeViewController (KIT-48)
}

actual fun requestNotificationPermission() {
    // TODO: Implement iOS UNUserNotificationCenter requestAuthorizationWithOptions (KIT-49)
}

actual fun scheduleBackgroundTasks() {
    // TODO: Implement iOS BGTaskScheduler (KIT-83)
}