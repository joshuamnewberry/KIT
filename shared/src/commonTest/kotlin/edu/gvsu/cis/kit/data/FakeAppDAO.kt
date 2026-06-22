package edu.gvsu.cis.kit.data

class FakeAppDAO : AppDAO {

    private val contacts = mutableListOf<Contact>()
    private val reminders = mutableListOf<CheckInReminder>()
    private val events = mutableListOf<Event>()
    private val importantDates = mutableListOf<ImportantDate>()
    private val reminderCrossRefs = mutableListOf<ReminderContactCrossRef>()
    private val eventCrossRefs = mutableListOf<EventContactCrossRef>()

    override suspend fun insertContact(contact: Contact) {
        contacts.removeAll { it.id == contact.id }
        contacts.add(contact)
    }

    override suspend fun updateContact(contact: Contact) {
        insertContact(contact)
    }

    override suspend fun deleteContact(contact: Contact) {
        contacts.removeAll { it.id == contact.id }
    }

    override suspend fun getAllContacts(): List<Contact> {
        return contacts.sortedBy { it.name }
    }

    override suspend fun getContactById(contactId: String): Contact? {
        return contacts.firstOrNull { it.id == contactId }
    }

    override suspend fun insertReminder(reminder: CheckInReminder) {
        reminders.removeAll { it.id == reminder.id }
        reminders.add(reminder)
    }

    override suspend fun updateReminder(reminder: CheckInReminder) {
        insertReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: CheckInReminder) {
        reminders.removeAll { it.id == reminder.id }
    }

    override suspend fun getRemindersForContact(contactId: String): List<CheckInReminder> {
        val reminderIds = reminderCrossRefs
            .filter { it.contactId == contactId }
            .map { it.reminderId }

        return reminders.filter { it.id in reminderIds }
    }

    override suspend fun getDueReminders(currentTimeMillis: Long): List<CheckInReminder> {
        return reminders.filter { it.nextReminderDate <= currentTimeMillis }
    }

    override suspend fun insertEvent(event: Event) {
        events.removeAll { it.id == event.id }
        events.add(event)
    }

    override suspend fun updateEvent(event: Event) {
        insertEvent(event)
    }

    override suspend fun deleteEvent(event: Event) {
        events.removeAll { it.id == event.id }
    }

    override suspend fun getEventsForContact(contactId: String): List<Event> {
        val eventIds = eventCrossRefs
            .filter { it.contactId == contactId }
            .map { it.eventId }

        return events.filter { it.id in eventIds }
    }

    override suspend fun insertReminderContactCrossRef(crossRef: ReminderContactCrossRef) {
        reminderCrossRefs.remove(crossRef)
        reminderCrossRefs.add(crossRef)
    }

    override suspend fun deleteReminderContactCrossRef(crossRef: ReminderContactCrossRef) {
        reminderCrossRefs.remove(crossRef)
    }

    override suspend fun insertEventContactCrossRef(crossRef: EventContactCrossRef) {
        eventCrossRefs.remove(crossRef)
        eventCrossRefs.add(crossRef)
    }

    override suspend fun deleteEventContactCrossRef(crossRef: EventContactCrossRef) {
        eventCrossRefs.remove(crossRef)
    }

    override suspend fun insertImportantDate(importantDate: ImportantDate) {
        importantDates.removeAll { it.id == importantDate.id }
        importantDates.add(importantDate)
    }

    override suspend fun updateImportantDate(importantDate: ImportantDate) {
        insertImportantDate(importantDate)
    }

    override suspend fun deleteImportantDate(importantDate: ImportantDate) {
        importantDates.removeAll { it.id == importantDate.id }
    }

    override suspend fun getImportantDatesForContact(contactId: String): List<ImportantDate> {
        return importantDates.filter { it.contactId == contactId }
    }

    override suspend fun getUpcomingImportantDates(upcomingTimeMillis: Long): List<ImportantDate> {
        return importantDates.filter { it.dateMillis <= upcomingTimeMillis }
    }

    override suspend fun getContactsForReminder(reminderId: String): List<Contact> {
        val contactIds = reminderCrossRefs
            .filter { it.reminderId == reminderId }
            .map { it.contactId }

        return contacts.filter { it.id in contactIds }
    }
}