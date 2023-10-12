package com.kronos.data.repository.user_custom_location

import com.kronos.data.data_source.weather.UserCustomLocationLocalDataSource
import com.kronos.domian.model.UserCustomLocation
import com.kronos.domian.repository.UserCustomLocationLocalRepository
import javax.inject.Inject

class UserCustomLocationLocalRepositoryImpl @Inject constructor(
    private val userCustomLocationLocalDataSource: UserCustomLocationLocalDataSource
) : UserCustomLocationLocalRepository {

    override suspend fun getSelectedLocation(): UserCustomLocation? {
        return userCustomLocationLocalDataSource.getSelectedLocation()
    }

    override suspend fun getCurrentLocation(): UserCustomLocation? {
        return userCustomLocationLocalDataSource.getCurrentLocation()
    }

    override suspend fun saveLocation(userCustomLocation: UserCustomLocation, isCurrent:Boolean): UserCustomLocation {
        return userCustomLocationLocalDataSource.saveLocation(userCustomLocation)
    }

    override suspend fun listAll(): List<UserCustomLocation> {
        return userCustomLocationLocalDataSource.listAll()
    }

    override suspend fun delete(userCustomLocation: UserCustomLocation): Boolean {
        return userCustomLocationLocalDataSource.delete(userCustomLocation)
    }

}