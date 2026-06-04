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

    @Query("SELECT * FROM check_in_reminders WHERE contactId = :contactId")
    suspend fun getRemindersForContact(contactId: String): List<CheckInReminder>

    @Query("SELECT * FROM check_in_reminders WHERE nextReminderDate <= :currentTimeMillis")
    suspend fun getDueReminders(currentTimeMillis: Long): List<CheckInReminder>

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
}

@Database(
    entities = [
        Contact::class,
        CheckInReminder::class,
        ImportantDate::class
    ],
    version = 1,
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
