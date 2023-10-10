package com.kronos.logger.di

import com.kronos.logger.LoggerImpl
import com.kronos.logger.interfaces.ILogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    abstract fun provideLogger(logger: LoggerImpl): ILogger

}