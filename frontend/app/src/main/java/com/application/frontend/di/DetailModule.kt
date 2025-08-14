package com.application.frontend.di

import com.application.frontend.data.detail.DetailApi
import com.application.frontend.data.detail.NetworkSubCategoryDetailRepository
import com.application.frontend.data.detail.SubCategoryDetailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetailModule {
    @Provides
    @Singleton
    fun provideDetailRepository(api: DetailApi): SubCategoryDetailRepository =
        NetworkSubCategoryDetailRepository(api)
}