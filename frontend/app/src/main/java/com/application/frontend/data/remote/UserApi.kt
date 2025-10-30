package com.application.frontend.data.remote

import retrofit2.http.GET

interface UserApi {
    @GET("/users/me")
    suspend fun getMe(): UserProfileResponseDto
}