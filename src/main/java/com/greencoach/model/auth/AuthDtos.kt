package com.greencoach.model.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class SignUpRequest(
    @field:NotBlank val nickname: String,
    @field:Email val email: String,
    @field:Size(min = 8) val password: String,
    val birth: LocalDate? = null,
    val gender: String? = null
)

data class LoginRequest(
    @field:Email val email: String,
    @field:NotBlank val password: String
)

data class LoginResponse(val token: String)

data class ForgotPasswordRequest(@field:Email val email: String)

data class NicknameCheckRequest(@field:NotBlank val nickname: String)

data class NicknameCheckResponse(val available: Boolean)

data class VerifyCodeRequest(
    @field:Email val email: String,
    @field:Size(min = 4, max = 4) val code: String
)

data class ResetPasswordRequest(
    @field:Email val email: String,
    @field:Size(min = 8) val newPassword: String
)

data class UserProfileDto(
    val id: Long,
    val nickname: String,
    val email: String,
    val verified: Boolean,
    val avatarUrl: String?,
    val birth: LocalDate?,
    val gender: String?
)