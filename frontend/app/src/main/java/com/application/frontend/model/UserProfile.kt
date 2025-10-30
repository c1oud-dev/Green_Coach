package com.application.frontend.model

data class UserProfile(
    val id: Long,
    val nickname: String,
    val email: String,
    val verified: Boolean,
    val avatarUrl: String?,
    val birth: String?,
    val gender: String?
)