package com.kronos.core.io.di

import com.kronos.core.io.storage.AbsoluteStorageImpl
import com.kronos.core.io.storage.InternalStorageImpl
import com.kronos.core.io.storage.PublicStorageImpl
import com.kronos.core.io.storage.ExternalStorageImpl
import com.kronos.core.io.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AbsoluteStorage

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalStorage

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicStorage

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalStorage

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @AbsoluteStorage
    @Singleton
    @Binds
    abstract fun provideAbsoluteStorage(impl: AbsoluteStorageImpl): Storage

    @InternalStorage
    @Singleton
    @Binds
    abstract fun provideInternalStorage(impl: InternalStorageImpl): Storage

    @PublicStorage
    @Singleton
    @Binds
    abstract fun providePublicStorage(impl: PublicStorageImpl): Storage

    @ExternalStorage
    @Singleton
    @Binds
    abstract fun provideExternalStorage(impl: ExternalStorageImpl): Storage

}