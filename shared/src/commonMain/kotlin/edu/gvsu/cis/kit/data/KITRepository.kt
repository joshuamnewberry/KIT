package edu.gvsu.cis.kit.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class KITRepositoryTest {

    @Test
    fun addContact_savesContactToDao() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        repository.addContact(
            name = "Jamie",
            phoneNumber = "6165551111",
            relationshipType = "Friend",
            address = "123 Main St",
            birthday = "2000-01-01"
        )

        val contacts = repository.getAllContacts()

        assertEquals(1, contacts.size)
        assertEquals("Jamie", contacts[0].name)
        assertEquals("6165551111", contacts[0].phoneNumber)
        assertEquals("Friend", contacts[0].relationshipType)
    }

    @Test
    fun getAllContacts_returnsMultipleContacts() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        repository.addContact("Alex", "6165550001", "Friend", "1 Oak Ave", "1999-05-05")
        repository.addContact("Zack", "6165550002", "Classmate", "2 Elm St", "1998-08-08")

        val contacts = repository.getAllContacts()

        assertEquals(2, contacts.size)
        assertEquals("Alex", contacts[0].name)
        assertEquals("Zack", contacts[1].name)
    }

    @Test
    fun getContactById_returnsCorrectContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        val contact = Contact(
            id = "contact-1",
            name = "Alex",
            phoneNumber = "6165551234"
        )

        dao.insertContact(contact)

        val result = repository.getContactById("contact-1")

        assertNotNull(result)
        assertEquals("Alex", result.name)
        assertEquals("6165551234", result.phoneNumber)
    }

    @Test
    fun getContactById_returnsNullForMissingContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        val result = repository.getContactById("missing-id")

        assertEquals(null, result)
    }

    @Test
    fun addReminder_savesReminderAndCrossReference() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Taylor"))

        repository.addReminder(
            contactIds = listOf("contact-1"),
            title = "Check in with Taylor",
            frequencyType = ReminderFrequencyType.DAILY,
            frequencyValue = null
        )

        val reminders = repository.getRemindersForContact("contact-1")

        assertEquals(1, reminders.size)
        assertEquals("Check in with Taylor", reminders[0].customMessage)
        assertEquals(ReminderFrequencyType.DAILY.name, reminders[0].frequencyType)
        assertFalse(reminders[0].isCompleted)
    }

    @Test
    fun addReminder_forMultipleContacts_linksReminderToEachContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Alex"))
        dao.insertContact(Contact(id = "contact-2", name = "Jamie"))

        repository.addReminder(
            contactIds = listOf("contact-1", "contact-2"),
            title = "Group check-in",
            frequencyType = ReminderFrequencyType.WEEKLY,
            frequencyValue = 1
        )

        val remindersForAlex = repository.getRemindersForContact("contact-1")
        val remindersForJamie = repository.getRemindersForContact("contact-2")

        assertEquals(1, remindersForAlex.size)
        assertEquals(1, remindersForJamie.size)
        assertEquals("Group check-in", remindersForAlex[0].customMessage)
        assertEquals("Group check-in", remindersForJamie[0].customMessage)
    }

    @Test
    fun getRemindersForContact_returnsEmptyList_whenContactHasNoReminders() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "No Reminder Contact"))

        val reminders = repository.getRemindersForContact("contact-1")

        assertEquals(0, reminders.size)
    }

    @Test
    fun addImportantDate_savesImportantDateForContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        repository.addImportantDate(
            contactId = "contact-1",
            title = "Birthday",
            dateMillis = 123456789L
        )

        val dates = repository.getImportantDatesForContact("contact-1")

        assertEquals(1, dates.size)
        assertEquals("Birthday", dates[0].title)
        assertEquals(123456789L, dates[0].dateMillis)
    }

    @Test
    fun logInteraction_createsEventForContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Morgan"))

        repository.logInteraction("contact-1")

        val events = dao.getEventsForContact("contact-1")

        assertEquals(1, events.size)
        assertEquals("Interaction Logged", events[0].title)
    }

    @Test
    fun getWeeklyInteractionCount_returnsDaoValue() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        val count = repository.getWeeklyInteractionCount()

        assertEquals(0, count)
    }
}        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
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
