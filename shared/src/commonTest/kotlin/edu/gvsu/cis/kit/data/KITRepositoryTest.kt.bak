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
            email = "jamie@email.com",
            relationshipType = "Friend",
            notes = "Met at school"
        )

        val contacts = repository.getAllContacts()

        assertEquals(1, contacts.size)
        assertEquals("Jamie", contacts[0].name)
        assertEquals("6165551111", contacts[0].phoneNumber)
        assertEquals("jamie@email.com", contacts[0].email)
        assertEquals("Friend", contacts[0].relationshipType)
        assertEquals("Met at school", contacts[0].notes)
    }

    @Test
    fun getAllContacts_returnsMultipleContacts() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        repository.addContact(name = "Alex")
        repository.addContact(name = "Jamie")

        val contacts = repository.getAllContacts()

        assertEquals(2, contacts.size)
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

        val result = repository.getContactById("does-not-exist")

        assertEquals(null, result)
    }

    @Test
    fun addReminder_savesReminderAndCrossReference() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Taylor"))

        repository.addReminder(
            contactIds = listOf("contact-1"),
            customMessage = "Check in with Taylor",
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
            customMessage = "Check in soon",
            frequencyType = ReminderFrequencyType.WEEKLY,
            frequencyValue = null
        )

        val alexReminders = repository.getRemindersForContact("contact-1")
        val jamieReminders = repository.getRemindersForContact("contact-2")

        assertEquals(1, alexReminders.size)
        assertEquals(1, jamieReminders.size)
        assertEquals("Check in soon", alexReminders[0].customMessage)
        assertEquals("Check in soon", jamieReminders[0].customMessage)
    }

    @Test
    fun getRemindersForContact_returnsEmptyList_whenContactHasNoReminders() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Alex"))

        val reminders = repository.getRemindersForContact("contact-1")

        assertEquals(0, reminders.size)
    }

    @Test
    fun addReminder_setsReminderAsNotCompleted() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        dao.insertContact(Contact(id = "contact-1", name = "Alex"))

        repository.addReminder(
            contactIds = listOf("contact-1"),
            customMessage = "Text Alex",
            frequencyType = ReminderFrequencyType.DAILY,
            frequencyValue = null
        )

        val reminders = repository.getRemindersForContact("contact-1")

        assertEquals(false, reminders[0].isCompleted)
    }

    @Test
    fun addImportantDate_savesImportantDateForContact() = runTest {
        val dao = FakeAppDAO()
        val repository = KITRepository(dao)

        repository.addImportantDate(
            contactId = "contact-1",
            title = "Birthday",
            type = ImportantDateType.BIRTHDAY,
            dateMillis = 123456789L,
            repeatsEveryYear = true
        )

        val dates = repository.getImportantDatesForContact("contact-1")

        assertEquals(1, dates.size)
        assertEquals("Birthday", dates[0].title)
        assertEquals(ImportantDateType.BIRTHDAY.name, dates[0].type)
        assertEquals(123456789L, dates[0].dateMillis)
        assertEquals(true, dates[0].repeatsEveryYear)
    }
}