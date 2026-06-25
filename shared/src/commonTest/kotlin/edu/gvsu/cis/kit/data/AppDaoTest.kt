package edu.gvsu.cis.kit.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AppDaoTest {

    // ---------- Contacts ----------

    @Test
    fun insertContact_thenGetContactById_returnsContact() = runTest {
        val dao = FakeAppDAO()
        val contact = Contact(
            id = "1",
            name = "Alex Johnson",
            phoneNumber = "6165551234",
            email = "alex@email.com"
        )
        dao.insertContact(contact)
        val result = dao.getContactById("1")
        assertNotNull(result)
        assertEquals("Alex Johnson", result.name)
        assertEquals("6165551234", result.phoneNumber)
        assertEquals("alex@email.com", result.email)
    }

    @Test
    fun getContactById_unknownId_returnsNull() = runTest {
        val dao = FakeAppDAO()
        assertEquals(null, dao.getContactById("does-not-exist"))
    }

    @Test
    fun getAllContacts_returnsContactsSortedByName() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "2", name = "Zack"))
        dao.insertContact(Contact(id = "1", name = "Alex"))
        val result = dao.getAllContacts()
        assertEquals("Alex", result[0].name)
        assertEquals("Zack", result[1].name)
    }

    @Test
    fun insertContact_sameId_replacesNotDuplicates() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "1", name = "Alex"))
        dao.insertContact(Contact(id = "1", name = "Alex Again"))
        val all = dao.getAllContacts()
        assertEquals(1, all.size)
        assertEquals("Alex Again", all[0].name)
    }

    @Test
    fun updateContact_changesStoredValues() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "1", name = "Alex"))
        dao.updateContact(Contact(id = "1", name = "Alex Updated"))
        val result = dao.getContactById("1")
        assertEquals("Alex Updated", result?.name)
    }

    @Test
    fun deleteContact_removesContact() = runTest {
        val dao = FakeAppDAO()
        val contact = Contact(id = "1", name = "Alex")
        dao.insertContact(contact)
        dao.deleteContact(contact)
        assertEquals(null, dao.getContactById("1"))
    }

    // ---------- Reminders ----------

    @Test
    fun insertReminderWithCrossRef_returnsReminderForContact() = runTest {
        val dao = FakeAppDAO()
        val contact = Contact(id = "contact-1", name = "Alex")
        val reminder = CheckInReminder(
            id = "reminder-1",
            customMessage = "Check in",
            frequencyType = ReminderFrequencyType.DAILY.name,
            frequencyValue = null,
            nextReminderDate = 1000L,
            isCompleted = false
        )
        dao.insertContact(contact)
        dao.insertReminder(reminder)
        dao.insertReminderContactCrossRef(
            ReminderContactCrossRef(reminderId = "reminder-1", contactId = "contact-1")
        )
        val result = dao.getRemindersForContact("contact-1")
        assertEquals(1, result.size)
        assertEquals("Check in", result[0].customMessage)
    }

    @Test
    fun getContactsForReminder_returnsLinkedContacts() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "c1", name = "Alex"))
        dao.insertContact(Contact(id = "c2", name = "Sam"))
        dao.insertContact(Contact(id = "c3", name = "Unlinked"))
        val reminder = CheckInReminder(
            id = "r1",
            customMessage = "Group check-in",
            frequencyType = ReminderFrequencyType.DAILY.name,
            frequencyValue = null,
            nextReminderDate = 500L,
            isCompleted = false
        )
        dao.insertReminder(reminder)
        dao.insertReminderContactCrossRef(ReminderContactCrossRef("r1", "c1"))
        dao.insertReminderContactCrossRef(ReminderContactCrossRef("r1", "c2"))
        val linked = dao.getContactsForReminder("r1")
        assertEquals(2, linked.size)
        assertTrue(linked.any { it.id == "c1" })
        assertTrue(linked.any { it.id == "c2" })
        assertFalse(linked.any { it.id == "c3" })
    }

    @Test
    fun getDueReminders_returnsOnlyRemindersAtOrBeforeNow() = runTest {
        val dao = FakeAppDAO()
        dao.insertReminder(
            CheckInReminder("past", "past", ReminderFrequencyType.DAILY.name, null, 100L, false)
        )
        dao.insertReminder(
            CheckInReminder("now", "now", ReminderFrequencyType.DAILY.name, null, 200L, false)
        )
        dao.insertReminder(
            CheckInReminder("future", "future", ReminderFrequencyType.DAILY.name, null, 999L, false)
        )
        val due = dao.getDueReminders(currentTimeMillis = 200L)
        assertEquals(2, due.size)
        assertTrue(due.any { it.id == "past" })
        assertTrue(due.any { it.id == "now" })
        assertFalse(due.any { it.id == "future" })
    }

    @Test
    fun updateReminder_changesStoredValues() = runTest {
        val dao = FakeAppDAO()
        dao.insertReminder(
            CheckInReminder("r1", "old", ReminderFrequencyType.DAILY.name, null, 100L, false)
        )
        dao.updateReminder(
            CheckInReminder("r1", "new", ReminderFrequencyType.DAILY.name, null, 100L, true)
        )
        val all = dao.getAllReminders()
        assertEquals(1, all.size)
        assertEquals("new", all[0].customMessage)
        assertTrue(all[0].isCompleted)
    }

    @Test
    fun deleteReminder_removesReminder() = runTest {
        val dao = FakeAppDAO()
        val reminder =
            CheckInReminder("r1", "x", ReminderFrequencyType.DAILY.name, null, 100L, false)
        dao.insertReminder(reminder)
        dao.deleteReminder(reminder)
        assertEquals(0, dao.getAllReminders().size)
    }

    @Test
    fun deleteReminderCrossRef_unlinksContactFromReminder() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "c1", name = "Alex"))
        dao.insertReminder(
            CheckInReminder("r1", "x", ReminderFrequencyType.DAILY.name, null, 100L, false)
        )
        val ref = ReminderContactCrossRef("r1", "c1")
        dao.insertReminderContactCrossRef(ref)
        dao.deleteReminderContactCrossRef(ref)
        assertEquals(0, dao.getRemindersForContact("c1").size)
    }

    // ---------- Events ----------

    @Test
    fun insertEventWithCrossRef_returnsEventForContact() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "c1", name = "Alex"))
        val event = Event(id = "e1")
        dao.insertEvent(event)
        dao.insertEventContactCrossRef(EventContactCrossRef(eventId = "e1", contactId = "c1"))
        val result = dao.getEventsForContact("c1")
        assertEquals(1, result.size)
        assertEquals("e1", result[0].id)
    }

    @Test
    fun deleteEvent_removesEvent() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "c1", name = "Alex"))
        val event = Event(id = "e1")
        dao.insertEvent(event)
        dao.insertEventContactCrossRef(EventContactCrossRef("e1", "c1"))
        dao.deleteEvent(event)
        assertEquals(0, dao.getEventsForContact("c1").size)
    }

    @Test
    fun deleteEventCrossRef_unlinksEventFromContact() = runTest {
        val dao = FakeAppDAO()
        dao.insertContact(Contact(id = "c1", name = "Alex"))
        dao.insertEvent(Event(id = "e1"))
        val ref = EventContactCrossRef("e1", "c1")
        dao.insertEventContactCrossRef(ref)
        dao.deleteEventContactCrossRef(ref)
        assertEquals(0, dao.getEventsForContact("c1").size)
    }

    // ---------- Important Dates ----------

    @Test
    fun insertImportantDate_thenGetForContact_returnsDate() = runTest {
        val dao = FakeAppDAO()
        dao.insertImportantDate(
            ImportantDate(id = "d1", contactId = "c1", dateMillis = 5000L)
        )
        val result = dao.getImportantDatesForContact("c1")
        assertEquals(1, result.size)
        assertEquals("d1", result[0].id)
    }

    @Test
    fun getImportantDatesForContact_filtersByContact() = runTest {
        val dao = FakeAppDAO()
        dao.insertImportantDate(ImportantDate(id = "d1", contactId = "c1", dateMillis = 1L))
        dao.insertImportantDate(ImportantDate(id = "d2", contactId = "c2", dateMillis = 2L))
        val result = dao.getImportantDatesForContact("c1")
        assertEquals(1, result.size)
        assertEquals("d1", result[0].id)
    }

    @Test
    fun getUpcomingImportantDates_returnsOnlyDatesAtOrBeforeThreshold() = runTest {
        val dao = FakeAppDAO()
        dao.insertImportantDate(ImportantDate(id = "soon", contactId = "c1", dateMillis = 100L))
        dao.insertImportantDate(ImportantDate(id = "later", contactId = "c1", dateMillis = 9999L))
        val result = dao.getUpcomingImportantDates(upcomingTimeMillis = 1000L)
        assertEquals(1, result.size)
        assertEquals("soon", result[0].id)
    }

    @Test
    fun updateImportantDate_changesStoredValues() = runTest {
        val dao = FakeAppDAO()
        dao.insertImportantDate(ImportantDate(id = "d1", contactId = "c1", dateMillis = 100L))
        dao.updateImportantDate(ImportantDate(id = "d1", contactId = "c1", dateMillis = 200L))
        val result = dao.getImportantDatesForContact("c1")
        assertEquals(1, result.size)
        assertEquals(200L, result[0].dateMillis)
    }

    @Test
    fun deleteImportantDate_removesDate() = runTest {
        val dao = FakeAppDAO()
        val date = ImportantDate(id = "d1", contactId = "c1", dateMillis = 100L)
        dao.insertImportantDate(date)
        dao.deleteImportantDate(date)
        assertEquals(0, dao.getImportantDatesForContact("c1").size)
    }
}
