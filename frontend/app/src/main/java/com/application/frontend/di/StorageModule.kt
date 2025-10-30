package com.application.frontend.di

import android.content.Context
import com.application.frontend.data.local.ScanHistoryStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideScanHistoryStorage(
        @ApplicationContext context: Context
    ): ScanHistoryStorage = ScanHistoryStorage(context)
}