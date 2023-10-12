package com.kronos.data.di

import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.user_custom_location.dao.UserCustomLocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    fun provideUserCustomLocationDao(@ApplicationDatabaseFactory factory: LocalDatabaseFactory): UserCustomLocationDao {
        return (factory.loadLocalDatabase() as ApplicationDatabase).userCustomLocationDao()
    }

}