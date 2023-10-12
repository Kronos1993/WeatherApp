package com.kronos.data.data_source.weather

import com.kronos.domian.model.UserCustomLocation


interface UserCustomLocationLocalDataSource {
    suspend fun getSelectedLocation(): UserCustomLocation?

    suspend fun getCurrentLocation(): UserCustomLocation?

    suspend fun saveLocation(userCustomLocation: UserCustomLocation, isCurrent:Boolean = false): UserCustomLocation

    suspend fun listAll(): List<UserCustomLocation>

    suspend fun delete(userCustomLocation: UserCustomLocation): Boolean
}