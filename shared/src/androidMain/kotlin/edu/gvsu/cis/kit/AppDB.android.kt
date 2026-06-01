package edu.gvsu.cis.kit

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.gvsu.cis.kit.data.AppDB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDB> {
    val appContext = context.applicationContext
    val dbFilePath = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<AppDB>(
        context = appContext,
        name = dbFilePath.absolutePath
    )
}