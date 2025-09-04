package com.application.frontend.di

import com.application.frontend.data.BackendApi
import com.application.frontend.data.CategoryApi
import com.application.frontend.data.co2.EmissionsApi
import com.application.frontend.data.community.CommunityApi
import com.application.frontend.data.community.CommunityRepository
import com.application.frontend.data.detail.DetailApi
import com.application.frontend.data.remote.AuthApi
import com.application.frontend.data.repository.AuthRepository
import com.application.frontend.data.repository.AuthRepositoryImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import javax.inject.Singleton
import okhttp3.mockwebserver.MockWebServer

/**
 * androidTest 전용: 실제 NetworkModule을 대체(오버라이드)하는 모듈.
 * baseUrl을 MockWebServer 주소로 맞춰서, 테스트에서 서버 응답을 제어한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {

    // 테스트가 문자열로 채워 넣는 베이스 URL
    object TestServerConfig {
        @Volatile var baseUrl: String = "http://127.0.0.1:1/" // placeholder
    }

    @Provides @Singleton
    fun provideMockWebServer(): MockWebServer = MockWebServer() // <-- 여기서는 start 안 함

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(
                Instant::class.java,
                JsonDeserializer { json, _, _ -> Instant.parse(json.asString) }
            )
            .create()

    @Provides @Singleton
    fun provideRetrofit(
        ok: OkHttpClient,
        gson: Gson
    ): Retrofit {
        // 테스트가 미리 채워넣은 문자열만 사용 (네트워크 작업 없음)
        val baseUrl = TestServerConfig.baseUrl
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(ok)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // MockWebServer 확장 함수 추가
    fun MockWebServer.isRunning(): Boolean {
        return try {
            this.hostName // 서버가 실행 중이면 호스트명을 반환
            true
        } catch (e: Exception) {
            false
        }
    }

    // 🔹 BackendApi (NewsViewModel 주입용)
    @Provides
    @Singleton
    fun provideBackendApi(retrofit: Retrofit): BackendApi =
        retrofit.create(BackendApi::class.java)


    // ✅ CategoryApi / DetailApi / EmissionsApi
    @Provides @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi =
        retrofit.create(CategoryApi::class.java)

    @Provides @Singleton
    fun provideDetailApi(retrofit: Retrofit): DetailApi =
        retrofit.create(DetailApi::class.java)

    @Provides @Singleton
    fun provideEmissionsApi(retrofit: Retrofit): EmissionsApi =
        retrofit.create(EmissionsApi::class.java)

    // ✅ CommunityApi / CommunityRepository
    @Provides @Singleton
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi =
        retrofit.create(CommunityApi::class.java)

    @Provides @Singleton
    fun provideCommunityRepository(api: CommunityApi): CommunityRepository =
        CommunityRepository(api)

    // ✅ AuthApi / AuthRepository (ProfileViewModel용)
    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideAuthRepository(api: AuthApi): AuthRepository =
        AuthRepositoryImpl(api)

}
