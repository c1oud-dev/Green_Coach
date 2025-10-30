package com.application.frontend.data.remote

data class UserProfileResponseDto(
    val id: Long,
    val nickname: String,
    val email: String,
    val verified: Boolean,
    val avatarUrl: String?,
    val birth: String?,
    val gender: String?
)