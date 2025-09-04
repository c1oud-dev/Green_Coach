package com.greencoach.service

import com.greencoach.model.auth.UserProfileDto
import com.greencoach.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getMe(): UserProfileDto {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: error("Unauthorized")
        val u = userRepository.findByEmail(email).orElseThrow()
        return UserProfileDto(
            id = u.id!!,
            nickname = u.nickname,
            email = u.email,
            verified = u.verified,
            avatarUrl = u.avatarUrl,
            birth = u.birth,
            gender = u.gender
        )
    }

    fun updateMe(nickname: String?, birth: String?, gender: String?): UserProfileDto {
        val email = SecurityContextHolder.getContext().authentication?.name ?: error("Unauthorized")
        val u = userRepository.findByEmail(email).orElseThrow()
        nickname?.let { u.nickname = it }
        birth?.let { u.birth = runCatching { java.time.LocalDate.parse(it) }.getOrNull() }
        gender?.let { u.gender = it }
        userRepository.save(u)
        return getMe()
    }
}