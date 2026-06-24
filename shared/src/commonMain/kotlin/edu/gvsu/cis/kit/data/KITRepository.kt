package edu.gvsu.cis.kit.data

import java.util.UUID

class KITRepository(
    private val dao: AppDAO
) {
    suspend fun getAllContacts(): List<Contact> = dao.getAllContacts()

    suspend fun getContactById(contactId: String): Contact? = dao.getContactById(contactId)

    suspend fun addContact(name: String, phoneNumber: String?, email: String?, relationshipType: String?) {
        val newContact = Contact(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            relationshipType = relationshipType
        )
        dao.insertContact(newContact)
    }

    suspend fun updateContact(contact: Contact) {
        dao.updateContact(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        dao.deleteContact(contact)
    }

    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder> = dao.getRemindersForContact(contactId)

    suspend fun addReminder(contactIds: List<String>, frequencyType: ReminderFrequencyType) {
        // Implementation logic for adding a reminder
    }

    suspend fun getImportantDatesForContact(contactId: String): List<ImportantDate> = dao.getImportantDatesForContact(contactId)

    suspend fun addImportantDate(contactId: String, title: String, type: ImportantDateType, dateMillis: Long) {
        val newDate = ImportantDate(
            id = UUID.randomUUID().toString(),
            contactId = contactId,
            title = title,
            type = type.name,
            dateMillis = dateMillis
        )
        dao.insertImportantDate(newDate)
    }
}