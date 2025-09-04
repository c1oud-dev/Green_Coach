package com.application.frontend.data.repository

import com.application.frontend.data.remote.AuthApi
import com.application.frontend.data.remote.LoginRequestDto
import javax.inject.Inject

object SessionToken {
    @Volatile var token: String? = null
}

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): String {
        val res = api.login(LoginRequestDto(email, password))
        // ⬇ 토큰 저장 (전역에서 읽어 헤더에 붙일 용도)
        SessionToken.token = res.token
        return res.token // 200 OK 가정(전역 에러는 Interceptor/try-catch)
    }
}
