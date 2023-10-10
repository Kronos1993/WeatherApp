package com.kronos.logger.di

import com.kronos.logger.exception.ExceptionHandler
import com.kronos.logger.exception.ExceptionHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExceptionHandlerModule {

    @Singleton
    @Binds
    abstract fun provideExceptionHandler(impl: ExceptionHandlerImpl): ExceptionHandler

}