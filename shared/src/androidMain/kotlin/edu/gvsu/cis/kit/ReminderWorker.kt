package edu.gvsu.cis.kit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()
            val dueReminders = dao.getDueReminders(currentTime)

            if (dueReminders.isNotEmpty()) {
                createNotificationChannel()

                dueReminders.forEach { reminder ->
                    val contacts = dao.getContactsForReminder(reminder.id)
                    val contactNames = contacts.joinToString { it.name }

                    val builder = NotificationCompat.Builder(applicationContext, "kit_reminders")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Time to Check In!")
                        .setContentText("Reach out to $contactNames: ${reminder.customMessage ?: "Scheduled reminder"}")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)

                    if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        NotificationManagerCompat.from(applicationContext).notify(reminder.id.hashCode(), builder.build())
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
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