package edu.gvsu.cis.kit.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ReminderFrequencyType {
    DAILY,
    WEEKLY,
    MONTHLY
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
    val address: String? = null,
    val relationshipType: String? = null,
    val notes: String? = null,
    val birthdayMillis: Long? = null,
    val lastContactedDate: Long? = null
)

@Entity(tableName = "check_in_reminders")
data class CheckInReminder(
    @PrimaryKey val id: String,
    val customMessage: String? = null,
    val frequencyType: String,
    val frequencyValue: Int? = null,
    val nextReminderDate: Long,
    val isCompleted: Boolean = false
)

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val title: String,
    val timestampMillis: Long
)

@Entity(
    tableName = "reminder_contact_cross_ref",
    primaryKeys = ["reminderId", "contactId"],
    indices = [Index("contactId")]
)
data class ReminderContactCrossRef(
    val reminderId: String,
    val contactId: String
)

@Entity(
    tableName = "event_contact_cross_ref",
    primaryKeys = ["eventId", "contactId"],
    indices = [Index("contactId")]
)
data class EventContactCrossRef(
    val eventId: String,
    val contactId: String
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