package com.application.frontend.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    val token: String
)

@JsonClass(generateAdapter = true)
data class SignUpRequestDto(
    val nickname: String,
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class NicknameCheckRequestDto(
    val nickname: String
)

@JsonClass(generateAdapter = true)
data class NicknameCheckResponseDto(
    val available: Boolean
)
