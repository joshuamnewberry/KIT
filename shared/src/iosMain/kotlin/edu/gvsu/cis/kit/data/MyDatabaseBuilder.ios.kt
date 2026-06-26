package edu.gvsu.cis.kit.data

import androidx.room.RoomDatabaseConstructor

actual object MyDatabaseBuilder : RoomDatabaseConstructor<AppDB> {
    actual override fun initialize(): AppDB {
        return getDatabaseInstance(getDatabaseBuilder())
    }
}
