package com.kronos.data.local.extensions

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kronos.core.io.PersistenceOptions
import com.kronos.core.io.getBasePath

class RoomDatabaseLoader {
    companion object {}
}

inline fun <reified T : RoomDatabase> RoomDatabaseLoader.Companion.loadLocalDatabase(
    appContext: Context,
    persistenceOptions: PersistenceOptions,
    databaseName: String,
    databaseFileName: String,
): T? {
    val persistencePath: String? = persistenceOptions.getBasePath().absolutePath

    var result: T? = null

    try {
        result = Room.databaseBuilder(appContext, T::class.java, "$persistencePath/$databaseFileName")
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

inline fun <reified T : RoomDatabase> RoomDatabaseLoader.Companion.createLocalDatabase(
    appContext: Context,
    persistenceOptions: PersistenceOptions,
    databaseFileName: String
): T {
    val persistencePath: String? = persistenceOptions.getBasePath().absolutePath
    return Room.databaseBuilder(appContext, T::class.java, "$persistencePath/$databaseFileName")
        .allowMainThreadQueries()
        .build()

}