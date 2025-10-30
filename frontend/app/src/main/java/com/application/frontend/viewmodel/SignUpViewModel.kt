package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NicknameCheckResult {
    AVAILABLE, UNAVAILABLE
}

data class SignUpUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isCheckingNickname: Boolean = false,
    val nicknameCheckResult: NicknameCheckResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
) {
    val canRequestNicknameCheck: Boolean
        get() = nickname.isNotBlank() && !isCheckingNickname && !isLoading

    val canSubmit: Boolean
        get() = nickname.isNotBlank() && email.isNotBlank() && password.length >= 8 &&
                nicknameCheckResult == NicknameCheckResult.AVAILABLE && !isLoading
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onNicknameChanged(value: String) {
        _uiState.update {
            it.copy(
                nickname = value,
                nicknameCheckResult = null,
                errorMessage = null
            )
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun checkNickname() {
        val current = _uiState.value
        if (!current.canRequestNicknameCheck) return
        _uiState.update { it.copy(isCheckingNickname = true, nicknameCheckResult = null) }
        viewModelScope.launch {
            try {
                val available = authRepository.checkNickname(current.nickname.trim())
                _uiState.update {
                    it.copy(
                        isCheckingNickname = false,
                        nicknameCheckResult = if (available) {
                            NicknameCheckResult.AVAILABLE
                        } else {
                            NicknameCheckResult.UNAVAILABLE
                        }
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isCheckingNickname = false,
                        errorMessage = t.message ?: "Nickname check failed"
                    )
                }
            }
        }
    }

    fun signUp() {
        val current = _uiState.value
        if (current.isLoading || !current.canSubmit) {
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.signUp(
                    nickname = current.nickname.trim(),
                    email = current.email.trim(),
                    password = current.password
                )
                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Sign up failed",
                        success = false
                    )
                }
            }
        }
    }

    fun consumeSuccess() {
        if (_uiState.value.success) {
            _uiState.update { it.copy(success = false) }
        }
    }

    fun consumeError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }
}