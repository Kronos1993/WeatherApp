package com.kronos.weatherapp.ui.settings.di

import com.kronos.weatherapp.ui.settings.WeatherOnSettingChangePublisher
import com.kronos.weatherapp.ui.settings.OnSettingChangePublisher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingEventModule {

    @Singleton
    @Binds
    abstract fun getSettingChangePublisher(impl: WeatherOnSettingChangePublisher): OnSettingChangePublisher
}