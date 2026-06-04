package edu.gvsu.cis.kit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReminderFrequency {
    WEEKLY,
    MONTHLY,
    QUARTERLY
}

enum class ImportantDateType {
    BIRTHDAY,
    ANNIVERSARY,
    CUSTOM
}

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val relationshipType: String? = null,
    val lastContactedDate: Long? = null
)

@Entity(tableName = "check_in_reminders")
data class CheckInReminder(
    @PrimaryKey val id: String,
    val contactId: String,
    val frequency: String,
    val nextReminderDate: Long,
    val isCompleted: Boolean = false
)

@Entity(tableName = "important_dates")
data class ImportantDate(
    @PrimaryKey val id: String,
    val contactId: String,
    val title: String,
    val type: String,
    val dateMillis: Long,
    val repeatsEveryYear: Boolean = true
)
