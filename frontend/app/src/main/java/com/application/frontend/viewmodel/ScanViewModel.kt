package com.application.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.frontend.data.repository.ScanRepository
import com.application.frontend.ui.screen.ScanResultDto
import com.application.frontend.ui.screen.ScanUiState
import com.application.frontend.ui.screen.ScanHistoryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        loadScanHistory()
    }

    // 새 메서드: 실제 이미지 업로드 → 분석 결과 수신 → 상태 업데이트
    fun analyzeImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 1) 이미지 분석 API
                val result = scanRepository.analyzeImage(imagePart)

                // 2) 결과 상태 반영
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    latestResult = result
                )

                // 3) 히스토리 서버 저장 시도 (실패해도 UI는 계속)
                runCatching {
                    scanRepository.saveScanResult(result)
                }

                // 4) 로컬 히스토리 UI 반영
                addToHistory(result)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "이미지 분석에 실패했습니다."
                )
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

    private fun loadScanHistory() {
        viewModelScope.launch {
            try {
                val history = scanRepository.getScanHistory()
                _uiState.value = _uiState.value.copy(scanHistory = history)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun addToHistory(result: ScanResultDto) {
        val newHistoryItem = ScanHistoryDto(
            id = System.currentTimeMillis(),
            category = result.category,
            scannedAt = getCurrentTimeString(),
            leafPoints = calculateLeafPoints(result),
            confirmed = true
        )

        val updatedHistory = listOf(newHistoryItem) + _uiState.value.scanHistory
        _uiState.value = _uiState.value.copy(scanHistory = updatedHistory)
    }

    private fun getCurrentTimeString(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy HH:mm a", java.util.Locale.ENGLISH)
        return sdf.format(java.util.Date())
    }

    private fun calculateLeafPoints(result: ScanResultDto): Int {
        // 한글 기준: subCategory가 있으면 우선, 없으면 category 사용
        val key = (result.subCategory ?: result.category).trim().lowercase()
        return when (key) {
            "투명 페트병" -> 2
            "일반 플라스틱" -> 2
            "비닐류" -> 1
            "스티로폼" -> 1
            "캔류" -> 2
            "고철류" -> 1
            "유리병" -> 2
            "종이류" -> 1
            "옷/섬유류" -> 3
            "대형 전자제품" -> 5
            "소형 전자제품" -> 3
            "전지류" -> 3
            "가구" -> 5
            else -> 0
        }
    }
}