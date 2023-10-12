package com.kronos.data.local

import androidx.room.RoomDatabase

interface LocalDatabaseFactory {
    fun  loadLocalDatabase(): RoomDatabase?
}