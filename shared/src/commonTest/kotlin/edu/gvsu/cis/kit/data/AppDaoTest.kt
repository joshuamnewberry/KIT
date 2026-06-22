package edu.gvsu.cis.kit.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AppDaoTest {

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
    fun getAllContacts_returnsContactsSortedByName() = runTest {
        val dao = FakeAppDAO()

        dao.insertContact(Contact(id = "2", name = "Zack"))
        dao.insertContact(Contact(id = "1", name = "Alex"))

        val result = dao.getAllContacts()

        assertEquals("Alex", result[0].name)
        assertEquals("Zack", result[1].name)
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

        val result = dao.getContactById("1")

        assertEquals(null, result)
    }

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
            ReminderContactCrossRef(
                reminderId = "reminder-1",
                contactId = "contact-1"
            )
        )

        val result = dao.getRemindersForContact("contact-1")

        assertEquals(1, result.size)
        assertEquals("Check in", result[0].customMessage)
    }
}