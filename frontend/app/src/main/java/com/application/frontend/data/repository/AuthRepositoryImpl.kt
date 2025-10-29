package com.application.frontend.data.repository

import com.application.frontend.data.remote.AuthApi
import com.application.frontend.data.remote.LoginRequestDto
import com.application.frontend.data.remote.NicknameCheckRequestDto
import com.application.frontend.data.remote.SignUpRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

object SessionToken {
    private val _token = MutableStateFlow<String?>(null)

    var token: String?
        get() = _token.value
        set(value) {
            _token.value = value
        }

    val tokenFlow: StateFlow<String?> = _token
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

    override suspend fun checkNickname(nickname: String): Boolean {
        val response = api.checkNickname(NicknameCheckRequestDto(nickname))
        return response.available
    }

    override suspend fun signUp(nickname: String, email: String, password: String) {
        api.signUp(SignUpRequestDto(nickname, email, password))
    }
}
