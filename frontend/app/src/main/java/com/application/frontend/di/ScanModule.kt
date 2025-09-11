package com.application.frontend.di

import com.application.frontend.data.ScanApi
import com.application.frontend.data.repository.ScanRepository
import com.application.frontend.data.repository.ScanRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanModule {

    @Provides
    @Singleton
    fun provideScanApi(retrofit: Retrofit): ScanApi =
        retrofit.create(ScanApi::class.java)

    @Provides
    @Singleton
    fun provideScanRepository(
        scanApi: ScanApi
    ): ScanRepository = ScanRepositoryImpl(scanApi)
}