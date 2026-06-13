import Foundation
import UserNotifications

final class IOSNotificationManager {
    static let shared = IOSNotificationManager()

    private init() {}

    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .badge, .sound]
        ) { granted, error in
            if let error = error {
                print("Notification permission error: \(error.localizedDescription)")
            }

            print("Notification permission granted: \(granted)")
        }
    }

    func scheduleCheckInReminder(
        contactName: String,
        secondsFromNow: TimeInterval
    ) {
        let content = UNMutableNotificationContent()
        content.title = "Time to check in"
        content.body = "Reach out to \(contactName) today."
        content.sound = .default

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: secondsFromNow,
            repeats: false
        )

        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Failed to schedule reminder: \(error.localizedDescription)")
            }
        }
    }

    func scheduleImportantDateReminder(
        title: String,
        contactName: String,
        secondsFromNow: TimeInterval
    ) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = "Important date for \(contactName) is coming up."
        content.sound = .default

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: secondsFromNow,
            repeats: false
        )

        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Failed to schedule important date reminder: \(error.localizedDescription)")
            }
        }
    }

    func cancelAllNotifications() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    }
}
