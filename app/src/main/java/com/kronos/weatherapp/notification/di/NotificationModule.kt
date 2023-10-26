package com.kronos.weatherapp.notification.di

import com.kronos.core.notification.INotifications
import com.kronos.weatherapp.notification.WeatherAppNotifications
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun provideNotification(implLocal: WeatherAppNotifications): INotifications

}