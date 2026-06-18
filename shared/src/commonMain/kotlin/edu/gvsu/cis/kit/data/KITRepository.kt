package edu.gvsu.cis.kit.data

import kotlin.random.Random

class KITRepository(
    private val dao: AppDAO
) {
    suspend fun getAllContacts(): List<Contact> {
        return dao.getAllContacts()
    }

    suspend fun addContact(
        name: String,
        phoneNumber: String? = null,
        email: String? = null,
        address: String? = null,
        relationshipType: String? = null,
        notes: String? = null,
        birthdayMillis: Long? = null
    ) {
        val contact = Contact(
            id = generateId(),
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            address = address,
            relationshipType = relationshipType,
            notes = notes,
            birthdayMillis = birthdayMillis,
            lastContactedDate = null
        )

        dao.insertContact(contact)
    }

    suspend fun getContactById(contactId: String): Contact? {
        return dao.getContactById(contactId)
    }

    suspend fun addReminder(
        contactIds: List<String>,
        customMessage: String? = null,
        frequencyType: ReminderFrequencyType,
        frequencyValue: Int? = null
    ) {
        val now = currentTimeMillis()

        // TODO: Update ReminderDateCalculator to support new Frequency Types and Values (KIT-70)
        val nextDate = now + 86400000 // Temporary logic placeholder

        val reminder = CheckInReminder(
            id = generateId(),
            customMessage = customMessage,
            frequencyType = frequencyType.name,
            frequencyValue = frequencyValue,
            nextReminderDate = nextDate,
            isCompleted = false
        )

        dao.insertReminder(reminder)

        contactIds.forEach { contactId ->
            dao.insertReminderContactCrossRef(
                ReminderContactCrossRef(reminder.id, contactId)
            )
        }
    }

    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder> {
        return dao.getRemindersForContact(contactId)
    }

    // TODO: Implement fetching logic to retrieve Events mapped to the visible calendar month (KIT-74)
    suspend fun getEventsForContact(contactId: String): List<Event> {
        return dao.getEventsForContact(contactId)
    }

    suspend fun addImportantDate(
        contactId: String,
        title: String,
        type: ImportantDateType,
        dateMillis: Long,
        repeatsEveryYear: Boolean = true
    ) {
        val importantDate = ImportantDate(
            id = generateId(),
            contactId = contactId,
            title = title,
            type = type.name,
            dateMillis = dateMillis,
            repeatsEveryYear = repeatsEveryYear
        )

        dao.insertImportantDate(importantDate)
    }

    suspend fun getImportantDatesForContact(contactId: String): List<ImportantDate> {
        return dao.getImportantDatesForContact(contactId)
    }

    // TODO: Implement "Clear All Data" database wipe logic (KIT-80)

    // TODO: Implement "Export Backup" (Database to JSON serialization) (KIT-81)

    // TODO: Implement "Import Backup" (JSON parsing to database batch insert) (KIT-82)

    private fun generateId(): String {
        return "${currentTimeMillis()}-${Random.nextInt(1000, 9999)}"
    }

    private fun currentTimeMillis(): Long {
        return kotlin.time.Clock.System.now().toEpochMilliseconds()
    }
}