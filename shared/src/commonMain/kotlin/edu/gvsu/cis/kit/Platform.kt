package edu.gvsu.cis.kit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// TODO: Implement KMP actual interfaces for requesting OS Contacts read permissions (KIT-46)
expect fun requestContactImport()

// TODO: Implement KMP actual interfaces for triggering native phone dialer intents (KIT-47)
expect fun triggerCallIntent(phoneNumber: String)

// TODO: Implement KMP actual interfaces for triggering native SMS/messaging intents (KIT-48)
expect fun triggerSmsIntent(phoneNumber: String)

// TODO: Implement KMP actual interfaces for requesting local notification scheduling permissions (KIT-49)
expect fun requestNotificationPermission()

// TODO: Implement background task scheduler (WorkManager for Android / BGTaskScheduler for iOS) (KIT-83)
expect fun scheduleBackgroundTasks()