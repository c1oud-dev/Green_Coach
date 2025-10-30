package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.repository.SavedPostRepository
import com.application.frontend.data.repository.SessionToken
import com.application.frontend.data.repository.UserContributionRepository
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
    private val userRepository: UserRepository,
    private val savedPostRepository: SavedPostRepository,
    private val userContributionRepository: UserContributionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState())
    val uiState: StateFlow<ProfileHomeUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        observeSavedPosts()
        observeMyPosts()
        observeMyReviews()
    }

    private fun observeSession() {
        viewModelScope.launch {
            SessionToken.tokenFlow.collectLatest { token ->
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileHomeUiState()
                    savedPostRepository.clear()
                    userContributionRepository.clear()
                } else {
                    loadProfile()
                }
            }
        }
    }

    private fun observeSavedPosts() {
        viewModelScope.launch {
            savedPostRepository.savedPosts.collectLatest { saved ->
                val savedMini = saved.map {
                    MiniPostUi(
                        id = it.id,
                        authorName = it.authorName,
                        content = it.content,
                        likes = it.likeCount,
                        comments = it.commentCount,
                        createdAtMillis = it.savedAt,
                        authorAvatarRes = null,
                        thumbnailRes = null
                    )
                }
                _uiState.update { state -> state.copy(saved = savedMini) }
            }
        }
    }

    private fun observeMyPosts() {
        viewModelScope.launch {
            userContributionRepository.myPosts.collectLatest { myPosts ->
                val posts = myPosts
                    .sortedByDescending { it.createdAtMillis }
                    .map { post ->
                        MiniPostUi(
                            id = post.id,
                            authorName = post.authorName,
                            content = post.content,
                            likes = post.likeCount,
                            comments = post.commentCount,
                            createdAtMillis = post.createdAtMillis,
                            authorAvatarRes = null,
                            thumbnailRes = null
                        )
                    }
                _uiState.update { state -> state.copy(posts = posts) }
            }
        }
    }

    private fun observeMyReviews() {
        viewModelScope.launch {
            userContributionRepository.myReviews.collectLatest { reviews ->
                val reviewUi = reviews
                    .sortedByDescending { it.createdAtMillis }
                    .map { review ->
                        ReviewUi(
                            content = review.content,
                            createdAtMillis = review.createdAtMillis
                        )
                    }
                _uiState.update { state -> state.copy(reviews = reviewUi) }
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