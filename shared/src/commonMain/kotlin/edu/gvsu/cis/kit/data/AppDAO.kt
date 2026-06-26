@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package edu.gvsu.cis.kit.data

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Dao
interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    suspend fun getContactById(contactId: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: CheckInReminder)

    @Update
    suspend fun updateReminder(reminder: CheckInReminder)

    @Delete
    suspend fun deleteReminder(reminder: CheckInReminder)

    @Query("SELECT * FROM check_in_reminders ORDER BY nextReminderDate ASC")
    suspend fun getAllReminders(): List<CheckInReminder>

    @Query("""
        SELECT check_in_reminders.* FROM check_in_reminders 
        INNER JOIN reminder_contact_cross_ref ON check_in_reminders.id = reminder_contact_cross_ref.reminderId 
        WHERE reminder_contact_cross_ref.contactId = :contactId
    """)
    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder>

    @Query("SELECT * FROM check_in_reminders WHERE nextReminderDate <= :currentTimeMillis")
    suspend fun getDueReminders(currentTimeMillis: Long): List<CheckInReminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("""
        SELECT events.* FROM events 
        INNER JOIN event_contact_cross_ref ON events.id = event_contact_cross_ref.eventId 
        WHERE event_contact_cross_ref.contactId = :contactId
    """)
    suspend fun getEventsForContact(contactId: String): List<Event>

    @Query("SELECT COUNT(*) FROM events WHERE timestampMillis >= :startOfWeekMillis")
    suspend fun getWeeklyInteractionCount(startOfWeekMillis: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderContactCrossRef(crossRef: ReminderContactCrossRef)

    @Delete
    suspend fun deleteReminderContactCrossRef(crossRef: ReminderContactCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventContactCrossRef(crossRef: EventContactCrossRef)

    @Delete
    suspend fun deleteEventContactCrossRef(crossRef: EventContactCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportantDate(importantDate: ImportantDate)

    @Update
    suspend fun updateImportantDate(importantDate: ImportantDate)

    @Delete
    suspend fun deleteImportantDate(importantDate: ImportantDate)

    @Query("SELECT * FROM important_dates WHERE contactId = :contactId")
    suspend fun getImportantDatesForContact(contactId: String): List<ImportantDate>

    @Query("SELECT * FROM important_dates WHERE dateMillis <= :upcomingTimeMillis")
    suspend fun getUpcomingImportantDates(upcomingTimeMillis: Long): List<ImportantDate>

    @Query("""
        SELECT contacts.* FROM contacts 
        INNER JOIN reminder_contact_cross_ref ON contacts.id = reminder_contact_cross_ref.contactId 
        WHERE reminder_contact_cross_ref.reminderId = :reminderId
    """)
    suspend fun getContactsForReminder(reminderId: String): List<Contact>
}

@Database(
    entities = [
        Contact::class, CheckInReminder::class, Event::class,
        ReminderContactCrossRef::class, EventContactCrossRef::class, ImportantDate::class
    ],
    version = 4,
    exportSchema = true
)
@ConstructedBy(MyDatabaseBuilder::class)
abstract class AppDB : RoomDatabase() {
    abstract fun getDao(): AppDAO
}

expect object MyDatabaseBuilder : RoomDatabaseConstructor<AppDB> {
    override fun initialize(): AppDB
}

fun getDatabaseInstance(builder: RoomDatabase.Builder<AppDB>): AppDB {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
