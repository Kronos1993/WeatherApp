package com.kronos.data.di

import com.kronos.data.data_source.user_custom_location.WeatherRemoteDataSource
import com.kronos.data.data_source.weather.UserCustomLocationLocalDataSource
import com.kronos.data.local.user_custom_location.UserCustomLocationLocalDataSourceImpl
import com.kronos.data.remote.WeatherRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun provideWeatherRemoteDataSource(implWeatherRemoteDataSource: WeatherRemoteDataSourceImpl): WeatherRemoteDataSource

    @Binds
    abstract fun provideUserCustomLocationLocalDataSource(userCustomLocationLocalDataSource: UserCustomLocationLocalDataSourceImpl): UserCustomLocationLocalDataSource

}
