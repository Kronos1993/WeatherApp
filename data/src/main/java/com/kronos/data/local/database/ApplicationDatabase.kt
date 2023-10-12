package com.kronos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kronos.data.local.user_custom_location.dao.UserCustomLocationDao
import com.kronos.data.local.user_custom_location.entity.UserCustomLocationEntity

@Database(
    entities = [UserCustomLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun userCustomLocationDao(): UserCustomLocationDao
}