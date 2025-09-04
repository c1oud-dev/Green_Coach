package com.application.frontend.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto
}
