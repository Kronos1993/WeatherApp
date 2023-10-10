package com.kronos.core.persistance.di

import com.kronos.core.io.PersistenceOptions
import com.kronos.core.persistance.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalPersistenceOptions

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalPersistenceOptions

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalDbPersistenceOptions

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalDbPersistenceOptions

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalLogsPersistenceOptions


@Module
@InstallIn(SingletonComponent::class)
abstract class PersistenceOptionsModule {

    @ExternalPersistenceOptions
    @Singleton
    @Binds
    abstract fun provideExternalPersistenceOptions(impl: ExternalPersistenceOptionsImpl): PersistenceOptions

    @InternalPersistenceOptions
    @Singleton
    @Binds
    abstract fun provideInternalWorkspacePersistenceOptions(impl: InternalPersistenceOptionsImpl): PersistenceOptions

    @ExternalDbPersistenceOptions
    @Singleton
    @Binds
    abstract fun provideExternalDbPersistenceOptions(impl: ExternalDbPersistenceOptionsImpl): PersistenceOptions


    @InternalDbPersistenceOptions
    @Singleton
    @Binds
    abstract fun provideInternalDbWorkspacePersistenceOptions(impl: InternalDbPersistenceOptionsImpl): PersistenceOptions

    @InternalLogsPersistenceOptions
    @Singleton
    @Binds
    abstract fun provideInternalLogsWorkspacePersistenceOptions(impl: InternalLogPersistenceOptionsImpl): PersistenceOptions
}
