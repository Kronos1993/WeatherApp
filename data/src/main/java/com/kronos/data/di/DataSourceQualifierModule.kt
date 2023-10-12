package com.kronos.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DataSourceQualifierModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RemoteApplicationDataSource

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalApplicationDataSource

}