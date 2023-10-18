package com.kronos.data.local.user_custom_location

import com.kronos.data.data_source.weather.UserCustomLocationLocalDataSource
import com.kronos.data.di.ApplicationDatabaseFactory
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.user_custom_location.mapper.toDomain
import com.kronos.data.local.user_custom_location.mapper.toEntity
import com.kronos.domian.model.UserCustomLocation
import javax.inject.Inject

class UserCustomLocationLocalDataSourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : UserCustomLocationLocalDataSource {

    override suspend fun listAll(): List<UserCustomLocation> {
        var result = listOf<UserCustomLocation>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.userCustomLocationDao().listAll().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun getSelectedLocation(): UserCustomLocation? {
        var result:UserCustomLocation? = UserCustomLocation()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.userCustomLocationDao().getSelectedLocation().let{
                it?.toDomain()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun getCurrentLocation(): UserCustomLocation? {
        var result:UserCustomLocation? = UserCustomLocation()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.userCustomLocationDao().getCurrentLocation().let{
                it?.toDomain()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun saveLocation(
        userCustomLocation: UserCustomLocation,
        isCurrent: Boolean
    ): UserCustomLocation {
        try {
            val entity = userCustomLocation.toEntity()
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.userCustomLocationDao().cleanSelectedLocation()
            internalDb.userCustomLocationDao().insertOrUpdate(entity)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return userCustomLocation
    }

    override suspend fun delete(userCustomLocation: UserCustomLocation): Boolean {
        var deleted = false
        try {
            val entity = userCustomLocation.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.userCustomLocationDao().deleteEvent(entity)
            deleted = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return deleted
    }

}
