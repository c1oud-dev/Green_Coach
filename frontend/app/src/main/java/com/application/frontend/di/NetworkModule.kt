package com.application.frontend.di

import com.application.frontend.BuildConfig
import com.application.frontend.data.CategoryApi
import com.application.frontend.data.co2.EmissionsApi
import com.application.frontend.data.community.CommunityApi
import com.application.frontend.data.community.CommunityRepository
import com.application.frontend.data.detail.DetailApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.Instant

/**
 * Retrofit 설정 파일
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            // (원하면 debug에만 로깅 인터셉터 추가)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson)) // ⬅ 커스텀 Gson 사용
            .build()

    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi =
        retrofit.create(CategoryApi::class.java)

    @Provides
    @Singleton
    fun provideDetailApi(retrofit: Retrofit): DetailApi =
        retrofit.create(DetailApi::class.java)

    @Provides
    @Singleton
    fun provideEmissionsApi(retrofit: Retrofit): EmissionsApi =
        retrofit.create(EmissionsApi::class.java)

    @Provides
    @Singleton
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi =
        retrofit.create(CommunityApi::class.java)

    @Provides
    @Singleton
    fun provideCommunityRepository(api: CommunityApi): CommunityRepository =
        CommunityRepository(api)

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(
                Instant::class.java,
                JsonDeserializer { json, _, _ -> Instant.parse(json.asString) } // "2025-09-02T.." → Instant
            )
            .create()

}