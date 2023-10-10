package com.kronos.data.di

import com.kronos.data.repository.weather.WeatherRemoteRepositoryImpl
import com.kronos.domian.repository.WeatherRemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideWeatherRemoteRepository(impl: WeatherRemoteRepositoryImpl): WeatherRemoteRepository

}
