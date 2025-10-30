package com.application.frontend.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("/auth/signup")
    suspend fun signUp(@Body body: SignUpRequestDto)

    @POST("/auth/nickname/check")
    suspend fun checkNickname(@Body body: NicknameCheckRequestDto): NicknameCheckResponseDto
}
