package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.repository.ScanRepository
import com.application.frontend.data.repository.SessionToken
import com.application.frontend.ui.screen.ScanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        loadLocalHistory()
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            SessionToken.tokenFlow.collectLatest { token ->
                val loggedIn = !token.isNullOrBlank()
                _uiState.update { it.copy(isLoggedIn = loggedIn) }
                if (loggedIn) {
                    refreshRemoteHistory()
                }
            }
        }
    }

    private fun loadLocalHistory() {
        viewModelScope.launch {
            val history = scanRepository.getLocalHistory()
            _uiState.update {
                it.copy(
                    scanHistory = history,
                    error = null,
                    isLoggedIn = isLoggedIn()
                )
            }
        }
    }

    // 새 메서드: 실제 이미지 업로드 → 분석 결과 수신 → 상태 업데이트
    fun analyzeImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1) 이미지 분석 API
                val result = scanRepository.analyzeImage(imagePart)

                // 2) 저장 결과 수신 및 UI 반영
                val savedEntry = scanRepository.saveScanResult(result)

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        latestResult = result,
                        scanHistory = listOf(savedEntry) + state.scanHistory.filterNot { it.id == savedEntry.id },
                        isLoggedIn = isLoggedIn()
                    )
                }

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "이미지 분석에 실패했습니다.",
                        isLoggedIn = isLoggedIn()
                    )
                }
            }
        }
    }

    // 에러를 외부(화면)에서 세팅할 수 있도록
    fun onError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(latestResult = null)
    }

    private fun refreshRemoteHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val history = scanRepository.getScanHistory()
                _uiState.update {
                    it.copy(
                        scanHistory = history,
                        isLoading = false,
                        error = null,
                        isLoggedIn = isLoggedIn()
                    )
                }
            } catch (http: HttpException) {
                if (http.code() == 401 || http.code() == 403) {
                    val local = scanRepository.getLocalHistory()
                    _uiState.update {
                        it.copy(
                            scanHistory = local,
                            isLoading = false,
                            error = null,
                            isLoggedIn = isLoggedIn()
                        )
                    }
                } else {
                    val local = scanRepository.getLocalHistory()
                    _uiState.update {
                        it.copy(
                            scanHistory = local,
                            isLoading = false,
                            error = http.message ?: "최근 스캔 기록을 불러오지 못했습니다.",
                            isLoggedIn = isLoggedIn()
                        )
                    }
                }
            } catch (e: Exception) {
                val local = scanRepository.getLocalHistory()
                _uiState.update {
                    it.copy(
                        scanHistory = local,
                        isLoading = false,
                        error = e.message ?: "최근 스캔 기록을 불러오지 못했습니다.",
                        isLoggedIn = isLoggedIn()
                    )
                }
            }
        }
    }

    fun refreshAuthState() {
        _uiState.update { it.copy(isLoggedIn = isLoggedIn()) }
        loadLocalHistory()
        if (isLoggedIn()) {
            refreshRemoteHistory()
        }
    }

    private fun isLoggedIn(): Boolean = !SessionToken.token.isNullOrBlank()
}
