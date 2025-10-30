package com.application.frontend.di

import com.application.frontend.BuildConfig
import com.application.frontend.data.BackendApi
import com.application.frontend.data.CategoryApi
import com.application.frontend.data.co2.EmissionsApi
import com.application.frontend.data.community.CommunityApi
import com.application.frontend.data.community.CommunityRepository
import com.application.frontend.data.detail.DetailApi
import com.application.frontend.data.remote.AuthApi
import com.application.frontend.data.remote.UserApi
import com.application.frontend.data.repository.AuthRepository
import com.application.frontend.data.repository.AuthRepositoryImpl
import com.application.frontend.data.repository.SessionToken
import com.application.frontend.data.repository.UserRepository
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
            // 🔹 개발 중 요청/응답 확인용 로깅 (원하면 buildType으로 레벨 분기)
            .addInterceptor(
                okhttp3.logging.HttpLoggingInterceptor().apply {
                    level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                }
            )
            // Authorization 헤더 자동 부착
            .addInterceptor { chain ->
                val original = chain.request()
                val path = original.url.encodedPath
                val token = SessionToken.token

                val req = if (!path.startsWith("/auth") && !token.isNullOrBlank()) {
                    original.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else original

                chain.proceed(req)
            }

            // 🔹 타임아웃 기본값 강화 (네트워크 품질 편차 대응)
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .readTimeout(java.time.Duration.ofSeconds(20))
            .writeTimeout(java.time.Duration.ofSeconds(20))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson)) // ⬅ 커스텀 Gson 사용
            .build()

    // 🔹 Auth API
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    // 🔹 BackendApi
    @Provides @Singleton
    fun provideBackendApi(retrofit: Retrofit): BackendApi =
        retrofit.create(BackendApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)


    // 🔹 Auth Repository (Interface -> Impl 바인딩)
    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi): AuthRepository =
        AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideUserRepository(api: UserApi): UserRepository =
        UserRepository(api)


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