package edu.gvsu.cis.kit

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import edu.gvsu.cis.kit.data.AppDAO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dao: AppDAO by inject()

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()
            val dueReminders = dao.getDueReminders(currentTime)

            if (dueReminders.isNotEmpty()) {
                createNotificationChannel()

                dueReminders.forEach { reminder ->
                    val contacts = dao.getContactsForReminder(reminder.id)
                    val contactNames = contacts.joinToString { it.name }

                    // Create an intent to launch the app
                    val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)?.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    // Wrap it in a PendingIntent
                    val pendingIntent = launchIntent?.let {
                        PendingIntent.getActivity(
                            applicationContext,
                            reminder.id.hashCode(), // Use reminder ID as request code to keep intents distinct
                            it,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }

                    // Format the text
                    val messageTitle = reminder.customMessage?.takeIf { it.isNotBlank() } ?: "Check-in Reminder"

                    val builder = NotificationCompat.Builder(applicationContext, "kit_reminders")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(messageTitle)
                        .setContentText("For: $contactNames")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true) // Clears the notification when tapped

                    // Attach the tap action
                    pendingIntent?.let { builder.setContentIntent(it) }

                    if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        NotificationManagerCompat.from(applicationContext).notify(reminder.id.hashCode(), builder.build())
                    }
                }
            }

            // RE-SCHEDULE FOR TOMORROW
            scheduleBackgroundTasks()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Try to reschedule even on failure so the loop doesn't break permanently
            scheduleBackgroundTasks()
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "kit_reminders",
                "Check-In Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for your scheduled check-ins"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}