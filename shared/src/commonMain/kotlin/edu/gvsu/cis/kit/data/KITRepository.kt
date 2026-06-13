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
        relationshipType: String? = null
    ) {
        val contact = Contact(
            id = generateId(),
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            relationshipType = relationshipType,
            lastContactedDate = null
        )

        dao.insertContact(contact)
    }

    suspend fun getContactById(contactId: String): Contact? {
        return dao.getContactById(contactId)
    }

    suspend fun addReminder(
        contactId: String,
        frequency: ReminderFrequency
    ) {
        val now = currentTimeMillis()
        val nextDate = ReminderDateCalculator.calculateNextReminderMillis(
            currentTimeMillis = now,
            frequency = frequency
        )

        val reminder = CheckInReminder(
            id = generateId(),
            contactId = contactId,
            frequency = frequency.name,
            nextReminderDate = nextDate,
            isCompleted = false
        )

        dao.insertReminder(reminder)
    }

    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder> {
        return dao.getRemindersForContact(contactId)
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

    private fun generateId(): String {
        return "${currentTimeMillis()}-${Random.nextInt(1000, 9999)}"
    }

    private fun currentTimeMillis(): Long {
        return kotlin.time.Clock.System.now().toEpochMilliseconds()
    }
}
