package edu.gvsu.cis.kit.data

import edu.gvsu.cis.kit.generateUUID
import edu.gvsu.cis.kit.getCurrentTimeMillis

class KITRepository(private val dao: AppDAO) {

    suspend fun getAllContacts(): List<Contact> = dao.getAllContacts()

    suspend fun getContactById(contactId: String): Contact? = dao.getContactById(contactId)

    suspend fun addContact(name: String, phoneNumber: String?, email: String?, relationshipType: String?, profilePictureUri: String? = null) {
        val newContact = Contact(
            id = generateUUID(),
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            relationshipType = relationshipType,
            profilePictureUri = profilePictureUri
        )
        dao.insertContact(newContact)
    }

    suspend fun updateContact(contact: Contact) { dao.updateContact(contact) }

    suspend fun deleteContact(contact: Contact) { dao.deleteContact(contact) }

    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder> = dao.getRemindersForContact(contactId)

    suspend fun getAllReminders(): List<CheckInReminder> = dao.getAllReminders()

    suspend fun getContactsForReminder(reminderId: String): List<Contact> = dao.getContactsForReminder(reminderId)

    suspend fun addReminder(contactIds: List<String>, title: String, frequencyType: ReminderFrequencyType, frequencyValue: Int? = null) {
        val reminderId = generateUUID()
        val reminder = CheckInReminder(
            id = reminderId,
            customMessage = title,
            frequencyType = frequencyType.name,
            frequencyValue = frequencyValue,
            nextReminderDate = getCurrentTimeMillis()
        )
        dao.insertReminder(reminder)
        contactIds.forEach { contactId ->
            dao.insertReminderContactCrossRef(ReminderContactCrossRef(reminderId, contactId))
        }
    }

    suspend fun updateReminder(reminder: CheckInReminder) { dao.updateReminder(reminder) }

    suspend fun deleteReminder(reminder: CheckInReminder) { dao.deleteReminder(reminder) }

    suspend fun logInteraction(contactId: String) {
        val eventId = generateUUID()
        val event = Event(id = eventId, title = "Interaction Logged", timestampMillis = getCurrentTimeMillis())
        dao.insertEvent(event)
        dao.insertEventContactCrossRef(EventContactCrossRef(eventId, contactId))
    }

    suspend fun getWeeklyInteractionCount(): Int {
        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
        return dao.getWeeklyInteractionCount(getCurrentTimeMillis() - oneWeekMillis)
    }

    suspend fun getImportantDatesForContact(contactId: String): List<ImportantDate> = dao.getImportantDatesForContact(contactId)

    suspend fun addImportantDate(contactId: String, title: String, dateMillis: Long) {
        val newDate = ImportantDate(
            id = generateUUID(),
            contactId = contactId,
            title = title,
            dateMillis = dateMillis
        )
        dao.insertImportantDate(newDate)
    }
}