@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package edu.gvsu.cis.kit.data

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Dao
interface AppDAO {
    // Add SQL Queries here

}

@Database(
    entities = [],
    version = 1,
    exportSchema = true
)

@ConstructedBy(MyDatabaseBuilder::class)
abstract class AppDB: RoomDatabase() {
    abstract fun getDao(): AppDAO
}

// THIS IS NOT ACTUALLY AN ERROR
expect object MyDatabaseBuilder: RoomDatabaseConstructor<AppDB> {
    override fun initialize(): AppDB
}

fun getDatabaseInstance(builder: RoomDatabase.Builder<AppDB>): AppDB {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}