package edu.gvsu.cis.kit.data

import androidx.room.RoomDatabaseConstructor
import edu.gvsu.cis.kit.getDatabaseBuilder

actual object MyDatabaseBuilder : RoomDatabaseConstructor<AppDB> {
    actual override fun initialize(): AppDB {
        return getDatabaseInstance(getDatabaseBuilder())
    }
}
