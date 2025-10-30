package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.repository.SessionToken
import com.application.frontend.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUi(
    val nickname: String,
    val email: String,
    val verified: Boolean,
    val avatarRes: Int? = null
)

data class MiniPostUi(
    val id: String,
    val authorName: String,
    val authorAvatarRes: Int? = null,
    val content: String,
    val likes: Int,
    val comments: Int,
    val createdAtMillis: Long,
    val thumbnailRes: Int? = null
)

data class ReviewUi(
    val content: String,
    val createdAtMillis: Long
)

data class ProfileHomeUiState(
    val profile: ProfileUi = ProfileUi(nickname = "", email = "", verified = false),
    val posts: List<MiniPostUi> = emptyList(),
    val saved: List<MiniPostUi> = emptyList(),
    val reviews: List<ReviewUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState())
    val uiState: StateFlow<ProfileHomeUiState> = _uiState.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            SessionToken.tokenFlow.collectLatest { token ->
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileHomeUiState()
                } else {
                    loadProfile()
                }
            }
        }
    }

    private suspend fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val profile = userRepository.getMe()
            _uiState.update {
                it.copy(
                    profile = ProfileUi(
                        nickname = profile.nickname,
                        email = profile.email,
                        verified = profile.verified
                    ),
                    isLoading = false
                )
            }
        } catch (throwable: Throwable) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun refreshProfile() {
        if (SessionToken.token.isNullOrBlank()) {
            _uiState.value = ProfileHomeUiState()
            return
        }
        viewModelScope.launch {
            loadProfile()
        }
    }

    fun updateProfile(profile: ProfileUi) {
        _uiState.update { it.copy(profile = profile) }
    }

    fun updatePosts(posts: List<MiniPostUi>) {
        _uiState.update { it.copy(posts = posts) }
    }

    fun updateSaved(posts: List<MiniPostUi>) {
        _uiState.update { it.copy(saved = posts) }
    }

    fun updateReviews(reviews: List<ReviewUi>) {
        _uiState.update { it.copy(reviews = reviews) }
    }
}