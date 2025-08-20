package com.application.frontend.di

import com.application.frontend.data.co2.Co2Repository
import com.application.frontend.data.co2.EmissionsApi
import com.application.frontend.data.co2.NetworkCo2Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Co2Module {

    @Provides @Singleton
    fun provideCo2Repository(
        api: EmissionsApi,
        io: CoroutineDispatcher
    ): Co2Repository = NetworkCo2Repository(api, io)
}
