package com.application.frontend.data.repository

import com.application.frontend.data.remote.UserApi
import com.application.frontend.model.UserProfile
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApi
) {
    suspend fun getMe(): UserProfile {
        val dto = api.getMe()
        return UserProfile(
            id = dto.id,
            nickname = dto.nickname,
            email = dto.email,
            verified = dto.verified,
            avatarUrl = dto.avatarUrl,
            birth = dto.birth,
            gender = dto.gender
        )
    }
}