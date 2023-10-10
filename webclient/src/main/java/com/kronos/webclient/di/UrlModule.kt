package com.kronos.webclient.di

import com.kronos.webclient.UrlProvider
import com.kronos.webclient.UrlProviderImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UrlModule {
    @Singleton
    @Binds
    abstract fun provideUrl(impl: UrlProviderImp): UrlProvider
}
