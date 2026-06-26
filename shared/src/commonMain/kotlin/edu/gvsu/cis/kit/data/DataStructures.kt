package edu.gvsu.cis.kit.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val relationshipType: String? = null,
    val address: String? = null,
    val birthday: String? = null,
    val notes: String? = null,
    val profilePictureUri: String? = null
)

@Serializable
@Entity(tableName = "check_in_reminders")
data class CheckInReminder(
    @PrimaryKey val id: String,
    val customMessage: String? = null,
    val frequencyType: String,
    val frequencyValue: Int? = null,
    val nextReminderDate: Long,
    val isCompleted: Boolean = false
)

enum class ReminderFrequencyType { DAILY, WEEKLY, MONTHLY}

@Serializable
@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val title: String,
    val timestampMillis: Long
)

@Serializable
@Entity(tableName = "important_dates")
data class ImportantDate(
    @PrimaryKey val id: String,
    val contactId: String,
    val title: String,
    val dateMillis: Long
)

@Entity(tableName = "reminder_contact_cross_ref", primaryKeys = ["reminderId", "contactId"])
data class ReminderContactCrossRef(val reminderId: String, val contactId: String)

@Entity(tableName = "event_contact_cross_ref", primaryKeys = ["eventId", "contactId"])
data class EventContactCrossRef(val eventId: String, val contactId: String)