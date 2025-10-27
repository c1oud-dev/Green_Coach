package com.application.frontend.data.repository

interface AuthRepository {
    /** 성공 시 JWT 토큰 문자열 반환 */
    suspend fun login(email: String, password: String): String

    /** true 면 사용 가능한 닉네임 */
    suspend fun checkNickname(nickname: String): Boolean

    /** 예외 없으면 성공 */
    suspend fun signUp(nickname: String, email: String, password: String)
}
