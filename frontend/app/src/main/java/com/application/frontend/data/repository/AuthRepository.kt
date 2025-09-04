package com.application.frontend.data.repository

interface AuthRepository {
    /** 성공 시 JWT 토큰 문자열 반환 */
    suspend fun login(email: String, password: String): String
}
